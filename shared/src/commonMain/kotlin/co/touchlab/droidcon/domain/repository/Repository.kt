package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.DomainEntity
import kotlinx.coroutines.flow.Flow

interface Repository<ID: Any, ENTITY: DomainEntity<ID>> {
    suspend fun get(id: ID): ENTITY

    fun observe(id: ID): Flow<ENTITY>

    fun observe(entity: ENTITY): Flow<ENTITY>

    suspend fun all(): List<ENTITY>

    fun observeAll(): Flow<List<ENTITY>>

    suspend fun add(entity: ENTITY)

    suspend fun remove(entity: ENTITY): Boolean

    suspend fun remove(id: ID): Boolean

    suspend fun update(entity: ENTITY)
}