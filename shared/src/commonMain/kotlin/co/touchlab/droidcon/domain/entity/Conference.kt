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
) : DomainEntity<Long>() {
    override val id: Long
        get() = requireNotNull(_id) { "Conference id cannot be null" }
}
