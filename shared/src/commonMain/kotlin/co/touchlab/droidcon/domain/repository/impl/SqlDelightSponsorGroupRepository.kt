package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.SponsorGroupQueries
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

class SqlDelightSponsorGroupRepository(
    private val sponsorGroupQueries: SponsorGroupQueries,
) : BaseRepository<SponsorGroup.Id, SponsorGroup>(), SponsorGroupRepository {

    override fun allSync(): List<SponsorGroup> = sponsorGroupQueries.selectAll(::sponsorGroupFactory).executeAsList()

    override fun observe(id: SponsorGroup.Id): Flow<SponsorGroup> {
        return sponsorGroupQueries.sponsorGroupByName(id.value, ::sponsorGroupFactory).asFlow().mapToOne()
    }

    override fun observeOrNull(id: SponsorGroup.Id): Flow<SponsorGroup?> {
        return sponsorGroupQueries.sponsorGroupByName(id.value, ::sponsorGroupFactory).asFlow().mapToOneOrNull()
    }

    override fun observeAll(): Flow<List<SponsorGroup>> {
        return sponsorGroupQueries.selectAll(::sponsorGroupFactory).asFlow().mapToList()
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
