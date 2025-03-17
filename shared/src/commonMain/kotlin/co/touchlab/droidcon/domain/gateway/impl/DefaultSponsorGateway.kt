package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultSponsorGateway(
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val profileRepository: ProfileRepository,
) : SponsorGateway {

    override fun observeSponsors(): Flow<List<SponsorGroupWithSponsors>> =
        sponsorGroupRepository.observeAll(Constants.conferenceId).map { groups ->
            groups.map { group ->
                SponsorGroupWithSponsors(
                    group,
                    sponsorRepository.allByGroupName(group.name, Constants.conferenceId),
                )
            }
        }

    override fun observeSponsorById(id: Sponsor.Id): Flow<Sponsor> = sponsorRepository.observe(id, Constants.conferenceId)

    override suspend fun getRepresentatives(sponsorId: Sponsor.Id): List<Profile> =
        profileRepository.getSponsorRepresentatives(sponsorId, Constants.conferenceId)
}
