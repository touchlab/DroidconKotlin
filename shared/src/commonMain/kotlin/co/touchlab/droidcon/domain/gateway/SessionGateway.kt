package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow

interface SessionGateway {

    fun observeSchedule(): Flow<List<ScheduleItem>>

    fun observeAgenda(): Flow<List<ScheduleItem>>

    fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem>

    suspend fun setAttending(session: Session, attending: Boolean)

    suspend fun setFeedback(session: Session, feedback: Session.Feedback)

    suspend fun getScheduleItem(id: Session.Id): ScheduleItem?
}
