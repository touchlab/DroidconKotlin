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

class SqlDelightRoomRepository(private val roomQueries: RoomQueries) :
    BaseRepository<Room.Id, Room>(),
    RoomRepository {

    override fun allSync(conferenceId: Long): List<Room> = roomQueries.selectAll(conferenceId, ::roomFactory).executeAsList()

    override fun observe(id: Room.Id, conferenceId: Long): Flow<Room> =
        roomQueries.selectById(id.value, conferenceId, ::roomFactory).asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: Room.Id, conferenceId: Long): Flow<Room?> =
        roomQueries.selectById(id.value, conferenceId, ::roomFactory).asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(conferenceId: Long): Flow<List<Room>> =
        roomQueries.selectAll(conferenceId, ::roomFactory).asFlow().mapToList(Dispatchers.Main)

    override fun doUpsert(entity: Room, conferenceId: Long) {
        roomQueries.upsert(
            id = entity.id.value,
            conferenceId = conferenceId,
            name = entity.name,
        )
    }

    override fun doDelete(id: Room.Id, conferenceId: Long) {
        roomQueries.deleteById(id.value, conferenceId)
    }

    override fun contains(id: Room.Id, conferenceId: Long): Boolean = roomQueries.existsById(id.value, conferenceId).executeAsOne() != 0L

    private fun roomFactory(id: Long, conferenceId: Long, name: String) = Room(id = Room.Id(id), name = name)
}
