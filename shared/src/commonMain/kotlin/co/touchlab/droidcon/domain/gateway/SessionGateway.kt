package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow

interface SessionGateway {

    fun observeSchedule(): Flow<List<ScheduleItem>>

    fun observeAgenda(): Flow<List<ScheduleItem>>

    fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem>

}
