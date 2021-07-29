package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session

interface SessionGateway {

    suspend fun getSchedule(): List<ScheduleItem>

    suspend fun getAgenda(): List<ScheduleItem>

    suspend fun getScheduleItem(id: Session.Id): ScheduleItem

}
