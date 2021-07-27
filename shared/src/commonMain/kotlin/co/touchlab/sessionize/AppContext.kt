package co.touchlab.sessionize

import co.touchlab.sessionize.SettingsKeys.KEY_FIRST_RUN
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.get

object AppContext: BaseModel() {

    fun initAppContext(networkRepo: NetworkRepo,
                       fileRepo: FileRepo,
                       notificationsApi: NotificationsApi,
                       notificationsModel: NotificationsModel) {

        notificationsApi.initializeNotifications { success ->
            if (success) {
                mainScope.launch {
                    notificationsModel.createNotifications()
                }
            } else {
                notificationsModel.cancelNotifications()
            }
        }

        mainScope.launch {
            maybeLoadSeedData(fileRepo)
            networkRepo.refreshData()
        }
    }

    private suspend fun maybeLoadSeedData(fileRepo: FileRepo) = withContext(backgroundDispatcher){
        try {
            if (firstRun) {
                fileRepo.seedFileLoad()
                updateFirstRun()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val firstRun: Boolean
        get() = get<Settings>().getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun() {
        get<Settings>().putBoolean(KEY_FIRST_RUN, false)
    }
}
