package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.QueryWrapper
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.createDatabaseManager
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import com.squareup.sqldelight.drivers.ios.NativeSqlDatabase
import com.squareup.sqldelight.drivers.ios.wrapConnection
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import platform.Foundation.*
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import timber.log.NSLogTree
import timber.log.Timber
import kotlin.native.concurrent.*
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
actual fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {

    val mainJobHolder = ThreadLocalRef<(B) -> Unit>()
    mainJobHolder.value = mainJob

    val worker = makeQueue("back")
    worker.execute(TransferMode.SAFE,
            { JobWrapper(backJob, mainJobHolder).freeze() }) {
        dispatch_async_f(dispatch_get_main_queue(), DetachedObjectGraph {
            Result(it.backJob(), it.mainJobLocal).freeze()
        }.asCPointer(), staticCFunction { it: COpaquePointer? ->
            initRuntimeIfNeeded()
            val result = DetachedObjectGraph<Result<B>>(it).attach()
            val mainProc = result.mainJobLocal.value!!
            mainProc(result.result)
        })
    }
}

data class Result<B>(val result: B, val mainJobLocal: ThreadLocalRef<(B) -> Unit>)
data class JobWrapper<B>(val backJob: () -> B, val mainJobLocal: ThreadLocalRef<(B) -> Unit>)

actual fun logException(t: Throwable) {
    t.printStackTrace()
}

actual fun settingsFactory(): Settings.Factory = PlatformSettings.Factory()

actual fun createUuid(): String = NSUUID.UUID().UUIDString

fun initTimber(priority: Int) {
    Timber.plant(NSLogTree(2))
}

actual fun initSqldelightDatabase(): SqlDatabase {
    return NativeSqlDatabase(
            createDatabaseManager(DatabaseConfiguration(
                    "droidconDb3",
                    1,
                    {
                        wrapConnection(it) {
                            QueryWrapper.Schema.create(it)
                        }
                    }
            ))
    )
}

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