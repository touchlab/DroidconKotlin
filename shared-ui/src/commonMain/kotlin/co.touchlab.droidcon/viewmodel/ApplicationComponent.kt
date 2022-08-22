package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.NotificationHandler
import co.touchlab.droidcon.util.DcDispatchers
import co.touchlab.droidcon.util.UrlHandler
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

class ApplicationComponent(
    componentContext: ComponentContext,
    private val dispatchers: DcDispatchers,
    private val tabFactory: TabComponent.Factory,
    private val feedbackDialogFactory: FeedbackDialogComponent.Factory,
    private val syncService: SyncService,
    private val notificationSchedulingService: NotificationSchedulingService,
    private val feedbackService: FeedbackService,
    private val settingsGateway: SettingsGateway,
    private val urlHandler: UrlHandler,
): ComponentContext by componentContext, NotificationHandler {

    private val navigation = StackNavigation<TabConfig>()

    private val _tabStack =
        childStack(
            source = navigation,
            initialConfiguration = TabConfig(TabComponent.Tab.Schedule),
            key = "tabs",
            childFactory = ::tabChild,
        )

    val tabStack: Value<ChildStack<*, TabComponent>> get() = _tabStack

    private val feedbackNavigation = StackNavigation<FeedbackConfig>()

    private val _feedbackStack = childStack(
        source = feedbackNavigation,
        initialConfiguration = FeedbackConfig.None,
        key = "feedback",
        childFactory = ::feedbackChild,
    )

    val feedbackStack: Value<ChildStack<*, FeedbackChild>> get() = _feedbackStack

    private val _model = MutableValue(Model(useComposeForIos = settingsGateway.settings().value.useComposeForIos))
    val model: Value<Model> get() = _model

    init {
        whileStarted(dispatchers.main) {
            notificationSchedulingService.runScheduling()
        }

        whileStarted(dispatchers.main) {
            syncService.runSynchronization()
        }

        whileStarted(dispatchers.main) {
            if (settingsGateway.settings().value.isFeedbackEnabled) {
                presentNextFeedback()
            }
        }

        whileStarted(dispatchers.main) {
            settingsGateway.settings().collect { settings ->
                _model.reduce { it.copy(useComposeForIos = settings.useComposeForIos) }
            }
        }
    }

    private fun tabChild(config: TabConfig, componentContext: ComponentContext): TabComponent =
        tabFactory.create(
            componentContext = componentContext,
            tab = config.tab,
            showFeedback = { feedbackNavigation.replaceCurrent(FeedbackConfig.FeedbackFromChild(it)) },
            showUrl = { urlHandler.openUrl(it.string) },
        )

    fun selectTab(tab: TabComponent.Tab) {
        navigation.navigate { stack ->
            stack.filterNot { it.tab == tab } + TabConfig(tab)
        }
    }

    override fun notificationReceived(sessionId: String, notificationType: NotificationService.NotificationType) {
        if (notificationType == NotificationService.NotificationType.Feedback) {
            whileStarted(dispatchers.main) {
                // We're not checking whether feedback is enabled, because the user opened a feedback notification.
                presentNextFeedback()
            }
        }
    }

    private fun feedbackChild(config: FeedbackConfig, componentContext: ComponentContext): FeedbackChild =
        when (config) {
            is FeedbackConfig.None -> FeedbackChild.None
            is FeedbackConfig.FeedbackFromNotification -> FeedbackChild.Feedback(feedbackFromNotification(config, componentContext))
            is FeedbackConfig.FeedbackFromChild -> FeedbackChild.Feedback(feedbackFromChild(config, componentContext))
        }

    private fun feedbackFromNotification(
        config: FeedbackConfig.FeedbackFromNotification,
        componentContext: ComponentContext,
    ): FeedbackDialogComponent =
        feedbackDialogFactory.create(
            componentContext = componentContext,
            session = config.session,
            submit = {
                feedbackService.submit(config.session, it)
                presentNextFeedback()
            },
            closeAndDisable = {
                settingsGateway.setFeedbackEnabled(false)
                feedbackNavigation.replaceCurrent(FeedbackConfig.None)
            },
            skip = {
                feedbackService.skip(config.session)
                presentNextFeedback()
            }
        )

    private suspend fun presentNextFeedback() {
        val nextSession = feedbackService.next()

        feedbackNavigation.replaceCurrent(
            if (nextSession != null) {
                FeedbackConfig.FeedbackFromNotification(nextSession)
            } else {
                FeedbackConfig.None
            }
        )
    }

    private fun feedbackFromChild(
        config: FeedbackConfig.FeedbackFromChild,
        componentContext: ComponentContext,
    ): FeedbackDialogComponent =
        feedbackDialogFactory.create(
            componentContext = componentContext,
            session = config.session,
            submit = {
                feedbackService.submit(config.session, it)
                feedbackNavigation.replaceCurrent(FeedbackConfig.None)
            },
            closeAndDisable = null,
            skip = {
                feedbackService.skip(config.session)
                feedbackNavigation.replaceCurrent(FeedbackConfig.None)
            }
        )

    data class Model(
        val useComposeForIos: Boolean,
    )

    @Parcelize
    private data class TabConfig(val tab: TabComponent.Tab): Parcelable

    sealed interface FeedbackChild {
        object None: FeedbackChild
        class Feedback(val component: FeedbackDialogComponent): FeedbackChild
    }

    @Parcelize
    private sealed interface FeedbackConfig: Parcelable {

        @Parcelize
        object None: FeedbackConfig

        @Parcelize
        data class FeedbackFromNotification(val session: Session): FeedbackConfig

        @Parcelize
        data class FeedbackFromChild(val session: Session): FeedbackConfig
    }
}
