package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor

interface ProfileRepository : Repository<Profile.Id, Profile> {

    suspend fun getSpeakersBySession(id: Session.Id, conferenceId: Long): List<Profile>

    fun setSessionSpeakers(session: Session, speakers: List<Profile.Id>, conferenceId: Long)

    fun setSponsorRepresentatives(sponsor: Sponsor, representatives: List<Profile.Id>, conferenceId: Long)

    suspend fun getSponsorRepresentatives(sponsorId: Sponsor.Id, conferenceId: Long): List<Profile>

    fun allSync(conferenceId: Long): List<Profile>
}
