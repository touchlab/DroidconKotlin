package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.domain.entity.DomainEntity
import co.touchlab.droidcon.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

abstract class BaseRepository<ID : Any, ENTITY : DomainEntity<ID>> : Repository<ID, ENTITY> {
    override suspend fun get(id: ID, conferenceId: Long): ENTITY = observe(id, conferenceId).first()

    override suspend fun find(id: ID, conferenceId: Long): ENTITY? = observeOrNull(id, conferenceId).first()

    override fun observe(entity: ENTITY, conferenceId: Long): Flow<ENTITY> = observe(entity.id, conferenceId)

    override suspend fun all(conferenceId: Long): List<ENTITY> = observeAll(conferenceId).first()

    override fun add(entity: ENTITY, conferenceId: Long) {
        if (!contains(entity.id, conferenceId)) {
            doUpsert(entity, conferenceId)
        } else {
            // TODO: Throw custom repository exception
            error("Can't insert entity: $entity which already exist in the database.")
        }
    }

    override fun remove(entity: ENTITY, conferenceId: Long) = remove(entity.id, conferenceId)

    override fun remove(id: ID, conferenceId: Long): Boolean {
        val idExists = contains(id, conferenceId)
        if (idExists) {
            doDelete(id, conferenceId)
        }
        return idExists
    }

    override fun update(entity: ENTITY, conferenceId: Long) {
        if (contains(entity.id, conferenceId)) {
            doUpsert(entity, conferenceId)
        } else {
            // TODO: Throw custom repository exception
            error("Can't update entity: $entity which doesn't exist in the database.")
        }
    }

    override fun addOrUpdate(entity: ENTITY, conferenceId: Long) = doUpsert(entity, conferenceId)

    protected abstract fun doUpsert(entity: ENTITY, conferenceId: Long)

    protected abstract fun doDelete(id: ID, conferenceId: Long)

    protected fun Long.toBoolean(): Boolean = this != 0L

    protected fun Boolean.toLong(): Long = if (this) {
        1L
    } else {
        0L
    }
}
