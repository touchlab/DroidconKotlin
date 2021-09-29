package co.touchlab.droidcon.domain.composite

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session

data class ScheduleItem(
    val session: Session,
    val isInConflict: Boolean,
    val room: Room?,
    val speakers: List<Profile>,
)