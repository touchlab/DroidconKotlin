package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.SponsorGroupQueries
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightSponsorGroupRepository(
    private val sponsorGroupQueries: SponsorGroupQueries,
) : BaseRepository<SponsorGroup.Id, SponsorGroup>(), SponsorGroupRepository {

    override fun allSync(): List<SponsorGroup> = sponsorGroupQueries.selectAll(::sponsorGroupFactory).executeAsList()

    override fun observe(id: SponsorGroup.Id): Flow<SponsorGroup> {
        return sponsorGroupQueries.sponsorGroupByName(id.value, ::sponsorGroupFactory).asFlow().mapToOne(Dispatchers.Main)
    }

    override fun observeOrNull(id: SponsorGroup.Id): Flow<SponsorGroup?> {
        return sponsorGroupQueries.sponsorGroupByName(id.value, ::sponsorGroupFactory).asFlow().mapToOneOrNull(Dispatchers.Main)
    }

    override fun observeAll(): Flow<List<SponsorGroup>> {
        return sponsorGroupQueries.selectAll(::sponsorGroupFactory).asFlow().mapToList(Dispatchers.Main)
    }

    override fun contains(id: SponsorGroup.Id): Boolean {
        return sponsorGroupQueries.existsByName(id.value).executeAsOne().toBoolean()
    }

    override fun doUpsert(entity: SponsorGroup) {
        sponsorGroupQueries.upsert(
            name = entity.id.value,
            displayPriority = entity.displayPriority,
            prominent = entity.isProminent,
        )
    }

    override fun doDelete(id: SponsorGroup.Id) {
        sponsorGroupQueries.deleteByName(id.value)
    }

    private fun sponsorGroupFactory(
        name: String,
        displayPriority: Int,
        isProminent: Boolean,
    ) = SponsorGroup(
        id = SponsorGroup.Id(name),
        displayPriority = displayPriority,
        isProminent = isProminent,
    )
}
