package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository

class DefaultSponsorGateway(
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
): SponsorGateway {

    override suspend fun getSponsors(): List<SponsorGroupWithSponsors> =
        sponsorGroupRepository.all().map { group ->
            SponsorGroupWithSponsors(
                group,
                sponsorRepository.allByGroupName(group.name)
            )
        }

    override suspend fun getSponsorById(id: Sponsor.Id): Sponsor = sponsorRepository.get(id)


}

