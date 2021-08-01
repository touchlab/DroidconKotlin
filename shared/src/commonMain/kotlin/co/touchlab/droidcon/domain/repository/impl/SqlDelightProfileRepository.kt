package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.ProfileQueries
import co.touchlab.droidcon.db.SessionSpeakerQueries
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.ProfileRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

class SqlDelightProfileRepository(
    private val profileQueries: ProfileQueries,
    private val speakerQueries: SessionSpeakerQueries,
): BaseRepository<Profile.Id, Profile>(), ProfileRepository {

    override suspend fun getSpeakersBySession(id: Session.Id): List<Profile> {
        return profileQueries.selectBySession(id.value, ::profileFactory).executeAsList()
    }

    override suspend fun setSessionSpeakers(session: Session, speakers: List<Profile.Id>) {
        speakerQueries.deleteBySessionId(session.id.value)
        speakers.forEachIndexed { index, speakerId ->
            speakerQueries.insertUpdate(
                sessionId = session.id.value,
                speakerId = speakerId.value,
                displayOrder = index.toLong(),
            )
        }
    }

    override fun observe(id: Profile.Id): Flow<Profile> {
        return profileQueries.selectById(id.value, ::profileFactory).asFlow().mapToOne()
    }

    override fun observeOrNull(id: Profile.Id): Flow<Profile?> {
        return profileQueries.selectById(id.value, ::profileFactory).asFlow().mapToOneOrNull()
    }

    override fun observeAll(): Flow<List<Profile>> {
        return profileQueries.selectAll(::profileFactory).asFlow().mapToList()
    }

    override suspend fun doUpsert(entity: Profile) {
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

    override suspend fun doDelete(id: Profile.Id) {
        profileQueries.delete(id.value)
    }

    override suspend fun contains(id: Profile.Id): Boolean {
        return profileQueries.existsById(id.value).executeAsOne().toBoolean()
    }

    private fun profileFactory(
        id: String,
        fullName: String,
        bio: String?,
        tagLine: String?,
        profilePicture: String?,
        twitter: String?,
        linkedIn: String?,
        website: String?
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
