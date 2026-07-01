package co.touchlab.droidcon.test

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
import kotlinx.datetime.TimeZone

object TestEntityFactory {
    fun room(id: Long = 1L, name: String = "Main Hall"): Room = Room(id = Room.Id(id), name = name)

    fun profile(
        id: String = "profile-1",
        fullName: String = "Jane Speaker",
        bio: String? = "Speaker bio",
        tagLine: String? = "Kotlin expert",
        profilePicture: Url? = Url("https://example.com/photo.jpg"),
        twitter: Url? = Url("https://twitter.com/jane"),
        linkedIn: Url? = null,
        website: Url? = Url("https://example.com"),
    ): Profile = Profile(
        id = Profile.Id(id),
        fullName = fullName,
        bio = bio,
        tagLine = tagLine,
        profilePicture = profilePicture,
        twitter = twitter,
        linkedIn = linkedIn,
        website = website,
    )

    fun sponsorGroup(name: String = "Gold", displayPriority: Int = 1, isProminent: Boolean = true): SponsorGroup = SponsorGroup(
        id = SponsorGroup.Id(name),
        displayPriority = displayPriority,
        isProminent = isProminent,
    )

    fun sponsor(
        name: String = "Touchlab",
        group: String = "Gold",
        hasDetail: Boolean = true,
        description: String? = "KMP specialists",
        icon: Url = Url("https://example.com/icon.png"),
        url: Url = Url("https://touchlab.co"),
    ): Sponsor = Sponsor(
        id = Sponsor.Id(name, group),
        hasDetail = hasDetail,
        description = description,
        icon = icon,
        url = url,
    )

    fun conference(
        id: Long? = null,
        name: String = "New Conference",
        timeZone: TimeZone = TimeZone.of("America/Chicago"),
        projectId: String = "new-project",
        collectionName: String = "new-collection",
        apiKey: String = "new-api-key",
        scheduleId: String = "new-schedule",
        selected: Boolean = false,
        active: Boolean = true,
        venueMap: String? = null,
    ): Conference = Conference(
        _id = id,
        name = name,
        timeZone = timeZone,
        projectId = projectId,
        collectionName = collectionName,
        apiKey = apiKey,
        scheduleId = scheduleId,
        selected = selected,
        active = active,
        venueMap = venueMap,
    )
}
