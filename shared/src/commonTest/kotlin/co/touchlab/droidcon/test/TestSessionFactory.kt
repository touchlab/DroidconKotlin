package co.touchlab.droidcon.test

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.impl.DefaultDateTimeService
import kotlin.time.Clock
import kotlin.time.Instant

object TestSessionFactory {
    private val testClock = object : Clock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1_700_000_000_000)
    }

    val dateTimeService: DateTimeService = DefaultDateTimeService(testClock)

    private val defaultStartsAt = Instant.fromEpochMilliseconds(1_700_000_000_000)
    private val defaultEndsAt = Instant.fromEpochMilliseconds(1_700_003_600_000)

    fun session(
        id: String = "session-1",
        title: String = "Test Session",
        description: String? = "Test description",
        startsAt: Instant = defaultStartsAt,
        endsAt: Instant = defaultEndsAt,
        isServiceSession: Boolean = false,
        rsvp: Session.RSVP = Session.RSVP(isAttending = false, isSent = false),
        feedback: Session.Feedback? = null,
    ): Session = Session(
        dateTimeService = dateTimeService,
        id = Session.Id(id),
        title = title,
        description = description,
        startsAt = startsAt,
        endsAt = endsAt,
        isServiceSession = isServiceSession,
        room = null,
        rsvp = rsvp,
        feedback = feedback,
    )
}
