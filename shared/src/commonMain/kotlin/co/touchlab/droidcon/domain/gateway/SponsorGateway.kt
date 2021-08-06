package co.touchlab.droidcon.domain.gateway

import co.touchlab.droidcon.domain.entity.SponsorGroup

interface SponsorGateway {
    suspend fun getSponsors(): List<SponsorGroup>
}