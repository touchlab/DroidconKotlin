package co.touchlab.droidcon.viewmodel.settings

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.viewmodel.binding
import co.touchlab.droidcon.viewmodel.instanceLock
import co.touchlab.droidcon.viewmodel.lifecycle
import co.touchlab.droidcon.viewmodel.managed
import co.touchlab.droidcon.viewmodel.observe
import co.touchlab.kermit.Logger

class SettingsViewModel(
    settingsGateway: SettingsGateway,
    private val aboutFactory: AboutViewModel.Factory,
    private val conferenceRepository: ConferenceRepository,
) : ViewModel() {
    private val log = Logger.withTag("SettingsViewModel")

    // Settings
    var isFeedbackEnabled by binding(
        settingsGateway.settings(),
        mapping = { it.isFeedbackEnabled },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setFeedbackEnabled(newValue)
            }
        },
    )
    val observeIsFeedbackEnabled by observe(::isFeedbackEnabled)

    var isRemindersEnabled by binding(
        settingsGateway.settings(),
        mapping = { it.isRemindersEnabled },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setRemindersEnabled(newValue)
            }
        },
    )
    val observeIsRemindersEnabled by observe(::isRemindersEnabled)

    // Conference management
    private val _allConferences = MutableObservableProperty<List<Conference>>(emptyList())
    val allConferences: ObservableProperty<List<Conference>> = _allConferences

    private val _selectedConference = MutableObservableProperty<Conference?>(null)
    val selectedConference: ObservableProperty<Conference?> = _selectedConference

    val about by managed(aboutFactory.create())

    override suspend fun whileAttached() {
        // Load conferences
        conferenceRepository.observeAll().collect { conferences ->
            _allConferences.value = conferences
        }

        // Load selected conference
        conferenceRepository.observeSelected().collect { conference ->
            _selectedConference.value = conference
        }
    }

    fun selectConference(conferenceId: Long) {
        log.d { "selectConference called with conferenceId: $conferenceId" }
        instanceLock.runExclusively {
            log.d { "About to call conferenceRepository.select($conferenceId)" }
            val result = conferenceRepository.select(conferenceId)
            log.d { "conferenceRepository.select() returned: $result" }
            // Force an immediate refresh of the selected conference
            // This is a workaround to ensure the UI updates promptly
            lifecycle.whileAttached {
                try {
                    val selectedConf = conferenceRepository.getSelected()
                    log.d { "Got updated conference: ${selectedConf.name} (ID: ${selectedConf.id})" }
                    _selectedConference.value = selectedConf
                } catch (e: Exception) {
                    log.e(e) { "Error getting selected conference after selection" }
                }
            }
        }
    }

    class Factory(
        private val settingsGateway: SettingsGateway,
        private val aboutFactory: AboutViewModel.Factory,
        private val conferenceRepository: ConferenceRepository,
    ) {
        fun create() = SettingsViewModel(settingsGateway, aboutFactory, conferenceRepository)
    }
}
