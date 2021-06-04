package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.KEY_FIRST_RUN
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.backgroundDispatcher
import co.touchlab.sessionize.platform.printThrowable
import kotlinx.coroutines.*

object AppContext {
    private val mainScope = MainScope()

    fun initAppContext(networkRepo: NetworkRepo = NetworkRepo,
                       fileRepo: FileRepo = FileRepo,
                       serviceRegistry: ServiceRegistry = ServiceRegistry,
                       dbHelper: SessionizeDbHelper = SessionizeDbHelper,
                       notificationsModel: NotificationsModel = NotificationsModel) {
        dbHelper.initDatabase(serviceRegistry.dbDriver)

        serviceRegistry.notificationsApi.initializeNotifications { success ->
            if (success) {
                mainScope.launch {
                    notificationsModel.createNotifications()
                }
            } else {
                notificationsModel.cancelNotifications()
            }
        }

        mainScope.launch {
            maybeLoadSeedData(fileRepo, serviceRegistry)
            networkRepo.refreshData()
        }
    }

    private suspend fun maybeLoadSeedData(fileRepo: FileRepo, serviceRegistry: ServiceRegistry) = withContext(ServiceRegistry.backgroundDispatcher){
        try {
            if (firstRun(serviceRegistry)) {
                fileRepo.seedFileLoad()
                updateFirstRun(serviceRegistry)
            }
        } catch (e: Exception) {
            printThrowable(e)
        }
    }

    private fun firstRun(serviceRegistry: ServiceRegistry): Boolean = serviceRegistry.appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun(serviceRegistry: ServiceRegistry) {
        serviceRegistry.appSettings.putBoolean(KEY_FIRST_RUN, false)
    }

    val backgroundContext = backgroundDispatcher()
}
