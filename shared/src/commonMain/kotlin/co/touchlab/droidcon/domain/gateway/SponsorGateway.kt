package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor

interface SponsorGateway {

    suspend fun getSponsors(): List<SponsorGroupWithSponsors>

    suspend fun getSponsorById(id: Sponsor.Id): Sponsor

    suspend fun getRepresentatives(sponsorId: Sponsor.Id): List<Profile>
}