package co.touchlab.droidcon.domain.entity

abstract class DomainEntity<ID : Any> {
    abstract val id: ID

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DomainEntity<*>

        if (id != other.id) return false

        return true
    }
}
