package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor

interface ProfileRepository: Repository<Profile.Id, Profile> {

    suspend fun getSpeakersBySession(id: Session.Id): List<Profile>

    fun setSessionSpeakers(session: Session, speakers: List<Profile.Id>)

    fun setSponsorRepresentatives(sponsor: Sponsor, representatives: List<Profile.Id>)

    suspend fun getSponsorRepresentatives(sponsorId: Sponsor.Id): List<Profile>

    fun allSync():List<Profile>
}