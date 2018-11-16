package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.QueryWrapper
import co.touchlab.multiplatform.architecture.threads.ThreadLocalImpl
import kotlin.system.getTimeMillis
import platform.darwin.*
import platform.Foundation.*
import kotlin.native.*
import kotlin.native.concurrent.*
import kotlinx.cinterop.*
import co.touchlab.sqliter.DatabaseConnection
import co.touchlab.sqliter.DatabaseMigration
import co.touchlab.sqliter.NativeDatabaseManager
import co.touchlab.sqliter.sqldelight.SQLiterConnection
import co.touchlab.sqliter.sqldelight.SQLiterHelper
import co.touchlab.stately.concurrency.ThreadLocalRef
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import timber.log.*

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

actual fun backgroundTask(backJob: () -> Unit) {
    backgroundTaskRun(backJob, "back")
}

actual fun networkBackgroundTask(backJob: () -> Unit) {
    backgroundTaskRun(backJob, "network")
}

private fun backgroundTaskRun(backJob: () -> Unit, key: String) {
    val worker = makeQueue(key)
    worker.execute(TransferMode.SAFE,
            { backJob.freeze() }) {
        it()
    }
}

actual fun simpleGet(url: String): String {
    val urlObj = NSURL(string = url)
    var resultString: String? = null
    val request = NSURLRequest.requestWithURL(urlObj)
    val data = NSURLConnection.sendSynchronousRequest(request, null, null)
    if (data != null) {
        val decoded = NSString.create(data = data, encoding = NSUTF8StringEncoding)
        if (decoded != null)
            resultString = decoded as String
    }

    if (resultString == null)
        throw NullPointerException("No network response")
    else
        return resultString!!
}

actual fun logException(t: Throwable) {
    t.printStackTrace()
}

actual fun settingsFactory(): Settings.Factory = PlatformSettings.Factory()

actual fun createUuid(): String = NSUUID.UUID().UUIDString

fun initTimber(priority: Int) {
    Timber.plant(NSLogTree(2))
}

actual fun initSqldelightDatabase(): SqlDatabase {
    return SQLiterHelper(NativeDatabaseManager(getDatabasePath("droidconDb2").path,
            object : DatabaseMigration {
                override fun onCreate(db: DatabaseConnection) {
                    QueryWrapper.onCreate(SQLiterConnection(db))
                }

                override fun onUpgrade(db: DatabaseConnection, oldVersion: Int, newVersion: Int) {
                    QueryWrapper.onMigrate(SQLiterConnection(db), oldVersion, newVersion)
                }

            },
            1
    ))
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

fun getDir(folder: String, mode: Int): File {
    return File(getDirPath(folder))
}

fun getDatabasePath(databaseName: String): File {
    return File(getDatabaseDirPath(), databaseName)
}