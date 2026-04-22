package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class DefaultSponsorGateway(
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val profileRepository: ProfileRepository,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SponsorGateway {

    private val log = Logger.withTag("DefaultSponsorGateway")

    override fun observeSponsors(): Flow<List<SponsorGroupWithSponsors>> {
        log.i { "observeSponsors" }

        return conferenceConfigProvider.observeChanges()
            .filterNotNull()
            .map { conference -> conference.id }
            .distinctUntilChanged()
            .flatMapLatest { id ->
                sponsorGroupRepository.observeAll(id).map { groups ->
                    groups.map { group ->
                        log.i { "Found a Group of Sponsors" }
                        SponsorGroupWithSponsors(
                            group,
                            sponsorRepository.allByGroupName(group.name, id),
                        )
                    }
                }
            }
/*
        val conferenceId = conferenceConfigProvider.getConferenceId()

        log.i { "Got the Conference ID" }
        return sponsorGroupRepository.observeAll(conferenceId).map { groups ->
            groups.map { group ->
                log.i { "Found a Group of Sponsors" }

                SponsorGroupWithSponsors(
                    group,
                    sponsorRepository.allByGroupName(group.name, conferenceId),
                )
            }
        }*/
    }

    override fun observeSponsorById(id: Sponsor.Id): Flow<Sponsor> = conferenceConfigProvider.observeChanges()
        .filterNotNull()
        .flatMapLatest { conference ->
            sponsorRepository.observe(id, conference.id)
        }

    override suspend fun getRepresentatives(sponsorId: Sponsor.Id): List<Profile> {
        val conferenceId = conferenceConfigProvider.getConferenceId()
        return profileRepository.getSponsorRepresentatives(sponsorId, conferenceId)
    }
}
