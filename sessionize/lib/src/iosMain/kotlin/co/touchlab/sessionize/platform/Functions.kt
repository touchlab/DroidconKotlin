package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.Database
import co.touchlab.sessionize.lateValue
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSThread
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.freeze
import kotlin.system.getTimeMillis

actual fun currentTimeMillis(): Long = getTimeMillis()

private val workerMap = HashMap<String, Worker?>()

//Multiple worker contexts get a copy of global state. Not sure about threads created outside of K/N (probably not)
//Lazy create ensures we don't try to create multiple queues
private fun makeQueue(key: String): Worker {
    var worker = workerMap.get(key)
    if (worker == null) {
        worker = Worker.start()
        workerMap.put(key, worker)
    }
    return worker!!
}

/**
 * This is 100% absolutely *not* how you should architect background tasks in K/N, but
 * we don't really have a lot of good examples, so here's one that will at least work.
 *
 * Expect everything you pass in to be frozen, and if that's not possible, it'll all fail. Just FYI.
 */
internal actual fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {

    val mainJobHolder = ThreadLocalRef<(B) -> Unit>()
    mainJobHolder.value = mainJob

    val worker = makeQueue("back")
    worker.execute(TransferMode.SAFE, { JobWrapper(backJob, mainJobHolder).freeze() }) { wrapper ->
        backToFront(wrapper.backJob, {
            wrapper.mainJobLocal.lateValue.invoke(it)
        })
    }
}

data class JobWrapper<B>(val backJob: () -> B, val mainJobLocal: ThreadLocalRef<(B) -> Unit>)

internal actual fun <B> backToFront(b: () -> B, job: (B) -> Unit) {
    dispatch_async_f(dispatch_get_main_queue(), DetachedObjectGraph {
        JobAndThing(job.freeze(), b())
    }.asCPointer(), staticCFunction { it: COpaquePointer? ->
        initRuntimeIfNeeded()
        val result = DetachedObjectGraph<Any>(it).attach() as JobAndThing<B>
        result.job(result.thing)
    })
}

internal data class JobAndThing<B>(val job: (B) -> Unit, val thing: B)

internal actual val mainThread: Boolean
    get() = NSThread.isMainThread

actual fun logException(t: Throwable) {
    t.printStackTrace()
}

actual fun createUuid(): String = NSUUID.UUID().UUIDString

@Suppress("unused")
fun defaultDriver(): SqlDriver = NativeSqliteDriver(Database.Schema, "sessionizedb")

@Suppress("unused")
fun defaultSettings(): Settings = PlatformSettings.Factory().create("DROIDCON_SETTINGS")

private fun getDirPath(folder: String): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true);
    val documentsDirectory = paths[0] as String;

    val databaseDirectory = documentsDirectory + "/$folder"

    val fileManager = NSFileManager.defaultManager()

    if (!fileManager.fileExistsAtPath(databaseDirectory))
        fileManager.createDirectoryAtPath(databaseDirectory, true, null, null); //Create folder

    return databaseDirectory
}

private fun getDatabaseDirPath(): String = getDirPath("databases")