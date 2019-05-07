package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.KEY_FIRST_RUN
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.logException
import kotlinx.serialization.json.Json

object AppContext {
    //Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/441
    private val primeJson = Json.nonstrict

    fun initAppContext(networkRepo: NetworkRepo = NetworkRepo,
                       fileRepo: FileRepo = FileRepo,
                       serviceRegistry: ServiceRegistry = ServiceRegistry,
                       dbHelper: SessionizeDbHelper = SessionizeDbHelper,
                       notificationsModel: NotificationsModel = NotificationsModel) {
        dbHelper.initDatabase(serviceRegistry.dbDriver)

        serviceRegistry.notificationsApi.initializeNotifications { success ->
            serviceRegistry.concurrent.backgroundTask({ success }, {
                if (it) {
                    notificationsModel.createNotificationsForSessions()
                } else {
                    notificationsModel.cancelNotificationsForSessions()
                }
            })
        }

        serviceRegistry.concurrent.backgroundTask({ maybeLoadSeedData(fileRepo, serviceRegistry) }) {
            networkRepo.refreshData()
        }
    }

    private fun maybeLoadSeedData(fileRepo: FileRepo, serviceRegistry: ServiceRegistry) {
        try {
            if (firstRun(serviceRegistry)) {
                fileRepo.seedFileLoad()
                updateFirstRun(serviceRegistry)
            }
            //If we do some kind of data re-load after a user logs in, we'll need to update this.
            //We assume for now that when the app first starts, you have nothing rsvp'd
            if(notificationsEnabled()) {
                createNotificationsForSessions()
            }

        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun firstRun(serviceRegistry: ServiceRegistry): Boolean = serviceRegistry.appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun(serviceRegistry: ServiceRegistry) {
        serviceRegistry.appSettings.putBoolean(KEY_FIRST_RUN, false)
    }
}
