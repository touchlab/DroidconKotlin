package co.touchlab.sessionize.platform

import kotlin.system.getTimeMillis
import platform.darwin.*
import platform.Foundation.*
import kotlin.native.*
import kotlin.native.concurrent.*
import co.touchlab.multiplatform.architecture.db.sqlite.*
import co.touchlab.knarch.*
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import timber.log.*

actual fun currentTimeMillis():Long = getTimeMillis()

//This definitely needs a rethink
actual fun initContext():NativeOpenHelperFactory {
    val context = DefaultSystemContext()
    val factory = IosNativeOpenHelperFactory(context)
    return factory
}

private var workerMap = HashMap<String, Worker?>()

//Multiple worker contexts get a copy of global state. Not sure about threads created outside of K/N (probably not)
//Lazy create ensures we don't try to create multiple queues
private fun makeQueue(key:String):Worker{
    var worker = workerMap.get(key)
    if(worker == null)
    {
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
actual fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit){

    val jobWrapper = JobWrapper(backJob, mainJob).freeze()

    val worker = makeQueue("back")
    worker.execute(TransferMode.SAFE,
            { jobWrapper }){
        val result  = DetachedObjectGraph<Any> { it.backJob().freeze() as Any }
        dispatch_async(dispatch_get_main_queue()){
            val mainResult = result.attach() as B
            it.mainJob(mainResult)
        }
    }
}

actual fun backgroundTask(backJob:()->Unit){
    backgroundTaskRun(backJob, "back")
}

actual fun networkBackgroundTask(backJob: () -> Unit) {
    backgroundTaskRun(backJob, "network")
}

private fun backgroundTaskRun(backJob: () -> Unit, key:String){
    val worker = makeQueue(key)
    worker.execute(TransferMode.SAFE,
            { backJob.freeze() }){
        it()
    }
}

actual fun <T> goFreeze(a:T):T = a.freeze()

data class JobWrapper<B>(val backJob:()-> B, val mainJob:(B) -> Unit)

actual fun simpleGet(url: String): String {
    val urlObj = NSURL(string = url)
    var resultString :String? = null
    val request = NSURLRequest.requestWithURL(urlObj)
    val data = NSURLConnection.sendSynchronousRequest(request, null, null)
    if(data != null){
        val decoded = NSString.create(data = data, encoding = NSUTF8StringEncoding)
        if(decoded != null)
            resultString = decoded as String
    }

    if(resultString == null)
        throw NullPointerException("No network response")
    else
        return resultString!!
}

actual fun logException(t: Throwable) {
    t.printStackTrace()
}

actual fun settingsFactory(): Settings.Factory  = PlatformSettings.Factory()

actual fun createUuid(): String  = NSUUID.UUID().UUIDString

fun initTimber(priority:Int){
    Timber.plant(NSLogTree(4))
}