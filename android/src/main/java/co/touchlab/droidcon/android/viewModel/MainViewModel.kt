package co.touchlab.droidcon.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.android.viewModel.feedback.FeedbackViewModel
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.service.FeedbackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel: ViewModel(), KoinComponent {

    private val feedbackService: FeedbackService by inject()
    private val settingsGateway: SettingsGateway by inject()

    val showFeedback: MutableStateFlow<FeedbackViewModel?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            if (settingsGateway.settings().value.isFeedbackEnabled) {
                presentNextFeedback()
            }
        }
    }

    private suspend fun presentNextFeedback() {
        showFeedback.value = feedbackService.next()?.let { session ->
            showFeedback.value?.apply {
                this.session.value = session
            } ?: FeedbackViewModel(
                MutableStateFlow(session),
                submitFeedback = { currentSession, feedback ->
                    feedbackService.submit(currentSession, feedback)
                    presentNextFeedback()
                },
                closeAndDisableFeedback = {
                    settingsGateway.setFeedbackEnabled(false)
                    showFeedback.value = null
                },
                skipFeedback = {
                    feedbackService.skip(it)
                    presentNextFeedback()
                },
            )
        }
    }
}