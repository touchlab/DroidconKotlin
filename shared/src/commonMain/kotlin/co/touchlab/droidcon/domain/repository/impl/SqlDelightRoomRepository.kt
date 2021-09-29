package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.impl.BaseRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

class SqlDelightRoomRepository(
    private val roomQueries: RoomQueries,
): BaseRepository<Room.Id, Room>(), RoomRepository {

    override fun observe(id: Room.Id): Flow<Room> {
        return roomQueries.selectById(id.value, ::roomFactory).asFlow().mapToOne()
    }

    override fun observeOrNull(id: Room.Id): Flow<Room?> {
        return roomQueries.selectById(id.value, ::roomFactory).asFlow().mapToOneOrNull()
    }

    override fun observeAll(): Flow<List<Room>> {
        return roomQueries.selectAll(::roomFactory).asFlow().mapToList()
    }

    override suspend fun doUpsert(entity: Room) {
        roomQueries.upsert(id = entity.id.value, name = entity.name)
    }

    override suspend fun doDelete(id: Room.Id) {
        roomQueries.deleteById(id.value)
    }

    override suspend fun contains(id: Room.Id): Boolean {
        return roomQueries.existsById(id.value).executeAsOne() != 0L
    }

    private fun roomFactory(id: Long, name: String) = Room(id = Room.Id(id), name = name)
}