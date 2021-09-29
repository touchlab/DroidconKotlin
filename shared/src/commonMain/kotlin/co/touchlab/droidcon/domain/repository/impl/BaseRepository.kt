package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.domain.entity.DomainEntity
import co.touchlab.droidcon.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

abstract class BaseRepository<ID: Any, ENTITY: DomainEntity<ID>>: Repository<ID, ENTITY> {
    override suspend fun get(id: ID): ENTITY {
        return observe(id).first()
    }

    override suspend fun find(id: ID): ENTITY? {
        return observeOrNull(id).first()
    }

    override fun observe(entity: ENTITY): Flow<ENTITY> {
        return observe(entity.id)
    }

    override suspend fun all(): List<ENTITY> {
        return observeAll().first()
    }

    override suspend fun add(entity: ENTITY) {
        if (!contains(entity.id)) {
            doUpsert(entity)
        } else {
            // TODO: Throw custom repository exception
            error("Can't insert entity: $entity which already exist in the database.")
        }
    }

    override suspend fun remove(entity: ENTITY) = remove(entity.id)

    override suspend fun remove(id: ID): Boolean {
        val idExists = contains(id)
        if (idExists) {
            doDelete(id)
        }
        return idExists
    }

    override suspend fun update(entity: ENTITY) {
        if (contains(entity.id)) {
            doUpsert(entity)
        } else {
            // TODO: Throw custom repository exception
            error("Can't update entity: $entity which doesn't exist in the database.")
        }
    }

    override suspend fun addOrUpdate(entity: ENTITY) = doUpsert(entity)

    protected abstract suspend fun doUpsert(entity: ENTITY)

    protected abstract suspend fun doDelete(id: ID)

    protected fun Long.toBoolean(): Boolean = this != 0L

    protected fun Boolean.toLong(): Long = if (this) {
        1L
    } else {
        0L
    }
}