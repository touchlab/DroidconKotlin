package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.DomainEntity
import kotlinx.coroutines.flow.Flow

interface Repository<ID: Any, ENTITY: DomainEntity<ID>> {
    suspend fun get(id: ID): ENTITY

    suspend fun find(id: ID): ENTITY?

    fun observe(id: ID): Flow<ENTITY>

    fun observeOrNull(id: ID): Flow<ENTITY?>

    fun observe(entity: ENTITY): Flow<ENTITY>

    suspend fun all(): List<ENTITY>

    fun observeAll(): Flow<List<ENTITY>>

    fun add(entity: ENTITY)

    fun remove(entity: ENTITY): Boolean

    fun remove(id: ID): Boolean

    fun update(entity: ENTITY)

    fun addOrUpdate(entity: ENTITY)

    fun contains(id: ID): Boolean
}