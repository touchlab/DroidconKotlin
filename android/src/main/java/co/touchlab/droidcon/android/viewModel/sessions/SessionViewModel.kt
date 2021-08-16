package co.touchlab.droidcon.android.viewModel.sessions

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session

class SessionViewModel(item: ScheduleItem) {

    val id: Session.Id = item.session.id

    val title: String = item.session.title
    val speakers: String = item.speakers.joinToString(", ") { it.fullName }

    val isAttending: Boolean = item.session.rsvp?.isAttending ?: false
    val isColliding: Boolean = item.isInConflict

    val isServiceSession: Boolean = item.session.isServiceSession
}