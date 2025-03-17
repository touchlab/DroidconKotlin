package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.db.SponsorGroupQueries
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightSponsorGroupRepository(private val sponsorGroupQueries: SponsorGroupQueries) :
    BaseRepository<SponsorGroup.Id, SponsorGroup>(),
    SponsorGroupRepository {

    override fun allSync(conferenceId: Long): List<SponsorGroup> = 
        sponsorGroupQueries.selectAll(conferenceId, ::sponsorGroupFactory).executeAsList()

    override fun observe(id: SponsorGroup.Id, conferenceId: Long): Flow<SponsorGroup> =
        sponsorGroupQueries.sponsorGroupByName(id.value, conferenceId, ::sponsorGroupFactory)
            .asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: SponsorGroup.Id, conferenceId: Long): Flow<SponsorGroup?> =
        sponsorGroupQueries.sponsorGroupByName(id.value, conferenceId, ::sponsorGroupFactory)
            .asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(conferenceId: Long): Flow<List<SponsorGroup>> =
        sponsorGroupQueries.selectAll(conferenceId, ::sponsorGroupFactory)
            .asFlow().mapToList(Dispatchers.Main)

    override fun contains(id: SponsorGroup.Id, conferenceId: Long): Boolean = 
        sponsorGroupQueries.existsByName(id.value, conferenceId).executeAsOne().toBoolean()

    override fun doUpsert(entity: SponsorGroup, conferenceId: Long) {
        sponsorGroupQueries.upsert(
            name = entity.id.value,
            conferenceId = conferenceId,
            displayPriority = entity.displayPriority,
            prominent = entity.isProminent,
        )
    }

    override fun doDelete(id: SponsorGroup.Id, conferenceId: Long) {
        sponsorGroupQueries.deleteByName(id.value, conferenceId)
    }

    private fun sponsorGroupFactory(name: String, conferenceId: Long, displayPriority: Int, isProminent: Boolean) = SponsorGroup(
        id = SponsorGroup.Id(name),
        displayPriority = displayPriority,
        isProminent = isProminent,
    )
}
