package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.viewmodel.session.AgendaComponent
import co.touchlab.droidcon.viewmodel.session.ScheduleComponent
import co.touchlab.droidcon.viewmodel.session.SessionDetailComponent
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailComponent
import co.touchlab.droidcon.viewmodel.settings.SettingsComponent
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailComponent
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

class TabComponent(
    componentContext: ComponentContext,
    val tab: Tab,
    private val scheduleFactory: ScheduleComponent.Factory,
    private val agendaFactory: AgendaComponent.Factory,
    private val sponsorsFactory: SponsorListComponent.Factory,
    private val settingsFactory: SettingsComponent.Factory,
    private val sessionDetailFactory: SessionDetailComponent.Factory,
    private val sponsorDetailFactory: SponsorDetailComponent.Factory,
    private val speakerDetailFactory: SpeakerDetailComponent.Factory,
    private val showFeedback: (Session) -> Unit,
    private val showUrl: (Url) -> Unit,
): ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Main(tab = tab),
            handleBackButton = true,
            childFactory = ::child,
        )

    val stack: Value<ChildStack<*, Child>> get() = _stack

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Main -> mainChild(config, componentContext)
            is Config.Session -> Child.Session(sessionDetailChild(config, componentContext))
            is Config.Sponsor -> Child.Sponsor(sponsorDetailChild(config, componentContext))
            is Config.Speaker -> Child.Speaker(speakerDetailChild(config))
        }

    private fun mainChild(config: Config.Main, componentContext: ComponentContext): Child =
        when (config.tab) {
            Tab.Schedule -> Child.Main.Schedule(scheduleChild(componentContext))
            Tab.Agenda -> Child.Main.Agenda(agendaChild(componentContext))
            Tab.Sponsors -> Child.Main.Sponsors(sponsorsChild(componentContext))
            Tab.Settings -> Child.Main.Settings(settingsChild(componentContext))
        }

    private fun scheduleChild(componentContext: ComponentContext): ScheduleComponent =
        scheduleFactory.create(
            componentContext = componentContext,
            sessionSelected = ::showSession,
        )

    private fun agendaChild(componentContext: ComponentContext): AgendaComponent =
        agendaFactory.create(
            componentContext = componentContext,
            sessionSelected = ::showSession,
        )

    private fun sponsorsChild(componentContext: ComponentContext): SponsorListComponent =
        sponsorsFactory.create(
            componentContext = componentContext,
            sponsorSelected = ::showSponsor,
        )

    private fun settingsChild(componentContext: ComponentContext): SettingsComponent =
        settingsFactory.create(
            componentContext = componentContext,
        )

    private fun sessionDetailChild(config: Config.Session, componentContext: ComponentContext): SessionDetailComponent =
        sessionDetailFactory.create(
            componentContext = componentContext,
            sessionId = config.sessionId,
            speakerSelected = ::showSpeaker,
            showFeedback = showFeedback,
            backPressed = navigation::pop,
        )

    private fun sponsorDetailChild(config: Config.Sponsor, componentContext: ComponentContext): SponsorDetailComponent =
        sponsorDetailFactory.create(
            componentContext = componentContext,
            sponsor = config.sponsor,
            speakerSelected = { navigation.push(Config.Speaker(it)) },
            backPressed = navigation::pop,
        )

    private fun speakerDetailChild(config: Config.Speaker): SpeakerDetailComponent =
        speakerDetailFactory.create(
            profile = config.profile,
            backPressed = navigation::pop,
        )

    private fun showSession(sessionId: Session.Id) {
        navigation.push(Config.Session(sessionId = sessionId))
    }

    private fun showSponsor(sponsor: Sponsor) {
        if (sponsor.hasDetail) {
            navigation.push(Config.Sponsor(sponsor = sponsor))
        } else {
            showUrl(sponsor.url)
        }
    }

    private fun showSpeaker(profile: Profile) {
        navigation.push(Config.Speaker(profile = profile))
    }

    class Factory(
        private val scheduleFactory: ScheduleComponent.Factory,
        private val agendaFactory: AgendaComponent.Factory,
        private val sponsorsFactory: SponsorListComponent.Factory,
        private val settingsFactory: SettingsComponent.Factory,
        private val sessionDetailFactory: SessionDetailComponent.Factory,
        private val sponsorDetailFactory: SponsorDetailComponent.Factory,
        private val speakerDetailFactory: SpeakerDetailComponent.Factory,
    ) {

        fun create(
            componentContext: ComponentContext,
            tab: Tab,
            showFeedback: (Session) -> Unit,
            showUrl: (Url) -> Unit,
        ): TabComponent =
            TabComponent(
                componentContext = componentContext,
                tab = tab,
                scheduleFactory = scheduleFactory,
                agendaFactory = agendaFactory,
                sponsorsFactory = sponsorsFactory,
                settingsFactory = settingsFactory,
                sessionDetailFactory = sessionDetailFactory,
                sponsorDetailFactory = sponsorDetailFactory,
                speakerDetailFactory = speakerDetailFactory,
                showFeedback = showFeedback,
                showUrl = showUrl,
            )
    }

    sealed interface Child {
        sealed interface Main: Child {
            class Schedule(val component: ScheduleComponent): Main
            class Agenda(val component: AgendaComponent): Main
            class Sponsors(val component: SponsorListComponent): Main
            class Settings(val component: SettingsComponent): Main
        }

        class Session(val component: SessionDetailComponent): Child
        class Sponsor(val component: SponsorDetailComponent): Child
        class Speaker(val component: SpeakerDetailComponent): Child
    }

    enum class Tab {
        Schedule, Agenda, Sponsors, Settings;
    }

    private sealed interface Config: Parcelable {
        @Parcelize
        data class Main(val tab: Tab): Config

        @Parcelize
        data class Session(val sessionId: co.touchlab.droidcon.domain.entity.Session.Id): Config

        @Parcelize
        data class Sponsor(val sponsor: co.touchlab.droidcon.domain.entity.Sponsor): Config

        @Parcelize
        data class Speaker(val profile: Profile): Config
    }
}
