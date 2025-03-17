package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Conference
import kotlinx.coroutines.flow.Flow

interface ConferenceRepository {
    fun observeAll(): Flow<List<Conference>>

    fun observeSelected(): Flow<Conference>

    suspend fun getSelected(): Conference

    suspend fun select(conferenceId: Long): Boolean

    suspend fun add(conference: Conference): Long

    suspend fun update(conference: Conference): Boolean

    suspend fun delete(conferenceId: Long): Boolean

    suspend fun initConferencesIfNeeded()
}
