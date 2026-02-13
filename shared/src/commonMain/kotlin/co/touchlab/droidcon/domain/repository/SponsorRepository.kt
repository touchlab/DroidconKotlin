package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Sponsor

interface SponsorRepository : Repository<Sponsor.Id, Sponsor> {
    suspend fun allByGroupName(group: String, conferenceId: Long): List<Sponsor>

    suspend fun allSync(conferenceId: Long): List<Sponsor>
}
