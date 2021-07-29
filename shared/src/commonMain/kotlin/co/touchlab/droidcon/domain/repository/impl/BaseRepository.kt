package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.domain.entity.DomainEntity
import co.touchlab.droidcon.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

abstract class BaseRepository<ID: Any, ENTITY: DomainEntity<ID>>: Repository<ID, ENTITY> {
    override suspend fun get(id: ID): ENTITY {
        return observe(id).first()
    }

    override fun observe(entity: ENTITY): Flow<ENTITY> {
        return observe(entity.id)
    }

    override suspend fun all(): List<ENTITY> {
        return observeAll().first()
    }

    override suspend fun add(entity: ENTITY) {
        if (!exists(entity.id)) {
            doUpsert(entity)
        } else {
            // TODO: Throw custom repository exception
            error("Can't insert entity: $entity which already exist in the database.")
        }
    }

    override suspend fun remove(entity: ENTITY) = remove(entity.id)

    override suspend fun remove(id: ID): Boolean {
        val idExists = exists(id)
        if (idExists) {
            doDelete(id)
        }
        return idExists
    }

    override suspend fun update(entity: ENTITY) {
        if (exists(entity.id)) {
            doUpsert(entity)
        } else {
            // TODO: Throw custom repository exception
            error("Can't update entity: $entity which doesn't exist in the database.")
        }
    }

    protected abstract fun doUpsert(entity: ENTITY)

    protected abstract fun doDelete(id: ID)

    protected abstract fun exists(id: ID): Boolean
}