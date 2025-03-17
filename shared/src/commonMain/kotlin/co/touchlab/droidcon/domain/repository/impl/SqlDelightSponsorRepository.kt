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

class SqlDelightSponsorRepository(private val sponsorQueries: SponsorQueries) :
    BaseRepository<Sponsor.Id, Sponsor>(),
    SponsorRepository {

    override fun observe(id: Sponsor.Id, conferenceId: Long): Flow<Sponsor> =
        sponsorQueries.sponsorById(id.name, id.group, conferenceId, ::sponsorFactory)
            .asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: Sponsor.Id, conferenceId: Long): Flow<Sponsor?> =
        sponsorQueries.sponsorById(id.name, id.group, conferenceId, ::sponsorFactory)
            .asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(conferenceId: Long): Flow<List<Sponsor>> =
        sponsorQueries.selectAll(conferenceId, ::sponsorFactory).asFlow().mapToList(Dispatchers.Main)

    override fun contains(id: Sponsor.Id, conferenceId: Long): Boolean =
        sponsorQueries.existsById(id.name, id.group, conferenceId).executeAsOne().toBoolean()

    override suspend fun allByGroupName(group: String, conferenceId: Long): List<Sponsor> =
        sponsorQueries.sponsorsByGroup(group, conferenceId, ::sponsorFactory).executeAsList()

    override fun allSync(conferenceId: Long): List<Sponsor> = sponsorQueries.selectAll(conferenceId, ::sponsorFactory).executeAsList()

    override fun doUpsert(entity: Sponsor, conferenceId: Long) {
        sponsorQueries.upsert(
            name = entity.id.name,
            groupName = entity.id.group,
            conferenceId = conferenceId,
            hasDetail = entity.hasDetail,
            description = entity.description,
            iconUrl = entity.icon.string,
            url = entity.url.string,
        )
    }

    override fun doDelete(id: Sponsor.Id, conferenceId: Long) {
        sponsorQueries.deleteById(id.name, id.group, conferenceId)
    }

    private fun sponsorFactory(
        name: String,
        groupName: String,
        conferenceId: Long,
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
