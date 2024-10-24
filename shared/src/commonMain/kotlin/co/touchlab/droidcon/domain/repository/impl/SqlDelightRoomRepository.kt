package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightRoomRepository(private val roomQueries: RoomQueries) : BaseRepository<Room.Id, Room>(), RoomRepository {

    override fun allSync(): List<Room> = roomQueries.selectAll(::roomFactory).executeAsList()

    override fun observe(id: Room.Id): Flow<Room> = roomQueries.selectById(id.value, ::roomFactory).asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: Room.Id): Flow<Room?> =
        roomQueries.selectById(id.value, ::roomFactory).asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(): Flow<List<Room>> = roomQueries.selectAll(::roomFactory).asFlow().mapToList(Dispatchers.Main)

    override fun doUpsert(entity: Room) {
        roomQueries.upsert(id = entity.id.value, name = entity.name)
    }

    override fun doDelete(id: Room.Id) {
        roomQueries.deleteById(id.value)
    }

    override fun contains(id: Room.Id): Boolean = roomQueries.existsById(id.value).executeAsOne() != 0L

    private fun roomFactory(id: Long, name: String) = Room(id = Room.Id(id), name = name)
}
