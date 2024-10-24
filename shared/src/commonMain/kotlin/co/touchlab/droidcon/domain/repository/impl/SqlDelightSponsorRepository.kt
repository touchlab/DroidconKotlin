package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.repository.SponsorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightSponsorRepository(private val sponsorQueries: SponsorQueries) : BaseRepository<Sponsor.Id, Sponsor>(), SponsorRepository {

    override fun observe(id: Sponsor.Id): Flow<Sponsor> =
        sponsorQueries.sponsorById(id.name, id.group, ::sponsorFactory).asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: Sponsor.Id): Flow<Sponsor?> =
        sponsorQueries.sponsorById(id.name, id.group, ::sponsorFactory).asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(): Flow<List<Sponsor>> = sponsorQueries.selectAll(::sponsorFactory).asFlow().mapToList(Dispatchers.Main)

    override fun contains(id: Sponsor.Id): Boolean = sponsorQueries.existsById(id.name, id.group).executeAsOne().toBoolean()

    override suspend fun allByGroupName(group: String): List<Sponsor> =
        sponsorQueries.sponsorsByGroup(group, ::sponsorFactory).executeAsList()

    override fun allSync(): List<Sponsor> = sponsorQueries.selectAll(::sponsorFactory).executeAsList()

    override fun doUpsert(entity: Sponsor) {
        sponsorQueries.upsert(
            name = entity.id.name,
            groupName = entity.id.group,
            hasDetail = entity.hasDetail,
            description = entity.description,
            iconUrl = entity.icon.string,
            url = entity.url.string,
        )
    }

    override fun doDelete(id: Sponsor.Id) {
        sponsorQueries.deleteById(id.name, id.group)
    }

    private fun sponsorFactory(name: String, groupName: String, hasDetail: Boolean, description: String?, iconUrl: String, url: String) =
        Sponsor(
            id = Sponsor.Id(name, groupName),
            hasDetail = hasDetail,
            description = description,
            icon = Url(iconUrl),
            url = Url(url),
        )
}
