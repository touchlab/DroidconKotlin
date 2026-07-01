package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Room

interface RoomRepository : Repository<Room.Id, Room> {
    suspend fun allSync(conferenceId: Long): List<Room>
}
