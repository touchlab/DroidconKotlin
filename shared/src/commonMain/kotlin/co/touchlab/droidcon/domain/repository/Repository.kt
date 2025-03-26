package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.DomainEntity
import kotlinx.coroutines.flow.Flow

interface Repository<ID : Any, ENTITY : DomainEntity<ID>> {
    suspend fun get(id: ID, conferenceId: Long): ENTITY

    suspend fun find(id: ID, conferenceId: Long): ENTITY?

    fun observe(id: ID, conferenceId: Long): Flow<ENTITY>

    fun observeOrNull(id: ID, conferenceId: Long): Flow<ENTITY?>

    fun observe(entity: ENTITY, conferenceId: Long): Flow<ENTITY>

    suspend fun all(conferenceId: Long): List<ENTITY>

    fun observeAll(conferenceId: Long): Flow<List<ENTITY>>

    fun add(entity: ENTITY, conferenceId: Long)

    fun remove(entity: ENTITY, conferenceId: Long): Boolean

    fun remove(id: ID, conferenceId: Long): Boolean

    fun update(entity: ENTITY, conferenceId: Long)

    fun addOrUpdate(entity: ENTITY, conferenceId: Long)

    fun contains(id: ID, conferenceId: Long): Boolean
}
