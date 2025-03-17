package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty
import org.brightify.hyperdrive.multiplatformx.property.ObservableProperty

class SettingsViewModel(
    settingsGateway: SettingsGateway,
    private val aboutFactory: AboutViewModel.Factory,
    private val conferenceRepository: ConferenceRepository,
) : BaseViewModel() {

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
        instanceLock.runExclusively {
            conferenceRepository.select(conferenceId)
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
