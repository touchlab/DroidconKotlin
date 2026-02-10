package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultSponsorGateway(
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val profileRepository: ProfileRepository,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SponsorGateway {

    override fun observeSponsors(): Flow<List<SponsorGroupWithSponsors>> {
        val conferenceId = conferenceConfigProvider.getConferenceId()
            ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return sponsorGroupRepository.observeAll(conferenceId).map { groups ->
            groups.map { group ->
                SponsorGroupWithSponsors(
                    group,
                    sponsorRepository.allByGroupName(group.name, conferenceId),
                )
            }
        }
    }

    override fun observeSponsorById(id: Sponsor.Id): Flow<Sponsor> {
        val conferenceId = conferenceConfigProvider.getConferenceId()
            ?: throw IllegalStateException("Conference ID is not available")
        return sponsorRepository.observe(id, conferenceId)
    }

    override suspend fun getRepresentatives(sponsorId: Sponsor.Id): List<Profile> {
        val conferenceId = conferenceConfigProvider.getConferenceId()
            ?: throw IllegalStateException("Conference ID is not available")
        return profileRepository.getSponsorRepresentatives(sponsorId, conferenceId)
    }
}
