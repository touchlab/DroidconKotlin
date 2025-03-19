package co.touchlab.droidcon.domain.entity

import kotlinx.datetime.TimeZone

data class Conference(
    private val _id: Long? = null,
    val name: String,
    val timeZone: TimeZone,
    val projectId: String,
    val collectionName: String,
    val apiKey: String,
    val scheduleId: String,
    val selected: Boolean = false,
    val active: Boolean = true,
) : DomainEntity<Long>() {
    val showVenueMap: Boolean = true //We'll need to add this to the table
    override val id: Long
        get() = requireNotNull(_id) { "Conference id cannot be null" }
}
