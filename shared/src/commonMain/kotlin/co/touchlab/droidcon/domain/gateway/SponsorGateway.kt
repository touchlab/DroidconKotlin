package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import kotlinx.coroutines.flow.Flow

interface SponsorGateway {

    fun observeSponsors(): Flow<List<SponsorGroupWithSponsors>>

    fun observeSponsorById(id: Sponsor.Id): Flow<Sponsor>

    suspend fun getRepresentatives(sponsorId: Sponsor.Id): List<Profile>
}