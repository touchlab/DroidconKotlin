package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.ProfileQueries
import co.touchlab.droidcon.db.SessionSpeakerQueries
import co.touchlab.droidcon.db.SponsorRepresentativeQueries
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightProfileRepository(
    private val profileQueries: ProfileQueries,
    private val speakerQueries: SessionSpeakerQueries,
    private val representativeQueries: SponsorRepresentativeQueries,
) : BaseRepository<Profile.Id, Profile>(), ProfileRepository {

    override suspend fun getSpeakersBySession(id: Session.Id): List<Profile> =
        profileQueries.selectBySession(id.value, ::profileFactory).executeAsList()

    override fun setSessionSpeakers(session: Session, speakers: List<Profile.Id>) {
        speakerQueries.deleteBySessionId(session.id.value)
        speakers.forEachIndexed { index, speakerId ->
            speakerQueries.insertUpdate(
                sessionId = session.id.value,
                speakerId = speakerId.value,
                displayOrder = index.toLong(),
            )
        }
    }

    override fun setSponsorRepresentatives(sponsor: Sponsor, representatives: List<Profile.Id>) {
        representativeQueries.deleteBySponsorId(sponsorName = sponsor.id.name, sponsorGroupName = sponsor.id.group)
        representatives.forEachIndexed { index, representativeId ->
            representativeQueries.insertUpdate(
                sponsorName = sponsor.name,
                sponsorGroupName = sponsor.id.group,
                representativeId = representativeId.value,
                displayOrder = index.toLong(),
            )
        }
    }

    override suspend fun getSponsorRepresentatives(sponsorId: Sponsor.Id): List<Profile> =
        profileQueries.selectBySponsor(sponsorName = sponsorId.name, sponsorGroupName = sponsorId.group, mapper = ::profileFactory)
            .executeAsList()

    override fun allSync(): List<Profile> = profileQueries.selectAll(mapper = ::profileFactory).executeAsList()

    override fun observe(id: Profile.Id): Flow<Profile> =
        profileQueries.selectById(id.value, ::profileFactory).asFlow().mapToOne(Dispatchers.Main)

    override fun observeOrNull(id: Profile.Id): Flow<Profile?> =
        profileQueries.selectById(id.value, ::profileFactory).asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAll(): Flow<List<Profile>> = profileQueries.selectAll(::profileFactory).asFlow().mapToList(Dispatchers.Main)

    override fun doUpsert(entity: Profile) {
        profileQueries.upsert(
            id = entity.id.value,
            fullName = entity.fullName,
            bio = entity.bio,
            tagLine = entity.tagLine,
            profilePicture = entity.profilePicture?.string,
            twitter = entity.twitter?.string,
            linkedIn = entity.linkedIn?.string,
            website = entity.website?.string,
        )
    }

    override fun doDelete(id: Profile.Id) {
        profileQueries.delete(id.value)
    }

    override fun contains(id: Profile.Id): Boolean = profileQueries.existsById(id.value).executeAsOne().toBoolean()

    private fun profileFactory(
        id: String,
        fullName: String,
        bio: String?,
        tagLine: String?,
        profilePicture: String?,
        twitter: String?,
        linkedIn: String?,
        website: String?,
    ) = Profile(
        id = Profile.Id(id),
        fullName = fullName,
        bio = bio,
        tagLine = tagLine,
        profilePicture = profilePicture?.let(::Url),
        twitter = twitter?.let(::Url),
        linkedIn = linkedIn?.let(::Url),
        website = website?.let(::Url),
    )
}
