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
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnit
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitTimeZone
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSThread
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
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

private val localNotificationDelegate = LocalNotificationDelegate()

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

actual fun settingsFactory(): Settings.Factory = PlatformSettings.Factory()

actual fun createUuid(): String = NSUUID.UUID().UUIDString



@ExperimentalUnsignedTypes
actual fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {

    var notificationTime = timeInMS - tenMinutesInMS
    var trigger:UNCalendarNotificationTrigger? = null
    if(notificationTime > Date(NSDate()).toLongMillis()){
        val date = NSDate.dateWithTimeIntervalSince1970(notificationTime / 1000.0)
        var dateFlags: NSCalendarUnit = NSCalendarUnitMonth.or(NSCalendarUnitDay).or(NSCalendarUnitYear)
        var timeFlags: NSCalendarUnit = NSCalendarUnitHour.or(NSCalendarUnitMinute).or(NSCalendarUnitSecond).or(NSCalendarUnitTimeZone)
        val dateInfo = NSCalendar.currentCalendar.components(dateFlags.or(timeFlags),date)

        trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateInfo, false)
    }

    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.delegate = localNotificationDelegate

    val content = UNMutableNotificationContent()
    content.setTitle(title)
    content.setBody(message)
    content.setSound(UNNotificationSound.defaultSound)

    val request = UNNotificationRequest.requestWithIdentifier(notificationId.toString(), content, trigger)
    center.addNotificationRequest(request,null)
}

actual fun cancelLocalNotification(notificationId: Int){
    val center = UNUserNotificationCenter.currentNotificationCenter()
    val identifiers:Array<String> = arrayOf(notificationId.toString())
    center.removePendingNotificationRequestsWithIdentifiers(identifiers.asList())
    center.removeDeliveredNotificationsWithIdentifiers(identifiers.asList())

}

actual fun initializeNotifications(){
}

actual fun deinitializeNotifications() {
}

// Needed to approve local notifications
class LocalNotificationDelegate : NSObject(),UNUserNotificationCenterDelegateProtocol{

    override fun userNotificationCenter(center: platform.UserNotifications.UNUserNotificationCenter,
                                        willPresentNotification: platform.UserNotifications.UNNotification,
                                        withCompletionHandler: (platform.UserNotifications.UNNotificationPresentationOptions) -> kotlin.Unit) {
        withCompletionHandler(UNNotificationPresentationOptionAlert)
    }
}


@Suppress("unused")
fun defaultDriver(): SqlDriver = NativeSqliteDriver(Database.Schema, "sessionizedb")

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