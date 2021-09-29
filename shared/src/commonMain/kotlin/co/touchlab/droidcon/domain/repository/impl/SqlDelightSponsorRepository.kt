package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.repository.SponsorRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

class SqlDelightSponsorRepository(
    private val sponsorQueries: SponsorQueries,
): BaseRepository<Sponsor.Id, Sponsor>(), SponsorRepository {

    override fun observe(id: Sponsor.Id): Flow<Sponsor> {
        return sponsorQueries.sponsorById(id.name, id.group, ::sponsorFactory).asFlow().mapToOne()
    }

    override fun observeOrNull(id: Sponsor.Id): Flow<Sponsor?> {
        return sponsorQueries.sponsorById(id.name, id.group, ::sponsorFactory).asFlow().mapToOneOrNull()
    }

    override fun observeAll(): Flow<List<Sponsor>> {
        return sponsorQueries.selectAll(::sponsorFactory).asFlow().mapToList()
    }

    override suspend fun contains(id: Sponsor.Id): Boolean {
        return sponsorQueries.existsById(id.name, id.group).executeAsOne().toBoolean()
    }

    override suspend fun allByGroupName(group: String): List<Sponsor> {
        return sponsorQueries.sponsorsByGroup(group, ::sponsorFactory).executeAsList()
    }

    override suspend fun doUpsert(entity: Sponsor) {
        sponsorQueries.upsert(
            name = entity.id.name,
            groupName = entity.id.group,
            hasDetail = entity.hasDetail,
            description = entity.description,
            iconUrl = entity.icon.string,
            url = entity.url.string,
        )
    }

    override suspend fun doDelete(id: Sponsor.Id) {
        sponsorQueries.deleteById(id.name, id.group)
    }

    private fun sponsorFactory(
        name: String,
        groupName: String,
        hasDetail: Boolean,
        description: String?,
        iconUrl: String,
        url: String,
    ) = Sponsor(
        id = Sponsor.Id(name, groupName),
        hasDetail = hasDetail,
        description = description,
        icon = Url(iconUrl),
        url = Url(url),
    )
}
