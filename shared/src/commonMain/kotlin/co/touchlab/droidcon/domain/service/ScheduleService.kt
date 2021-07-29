package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Session

interface ScheduleService {

    fun isInConflict(session: Session): Boolean

}