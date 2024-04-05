package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


object RSVPSDto {


    @Serializable
    data class RSVPsCollectionDto(
        val fields: Fields,
    ) {
        val sessionStringList: List<String>
            get() = fields.sessions.arrayValue.values.map { it.stringValue }

        fun copyWithSession(sessionId: String, shouldAdd: Boolean): RSVPsCollectionDto {
            val mutableSession = sessionStringList.toMutableList()
            mutableSession.removeAll { it == sessionId }
            if (shouldAdd) mutableSession.add(sessionId)

            return this.copy(
                Fields(
                    sessions = Sessions(mutableSession.toList().map { Value(it) })
                )
            )
        }
    }

    @Serializable
    data class Fields(
        val sessions: Sessions,
    )

    @Serializable
    data class Sessions(
        val arrayValue: ArrayValue,
    ) {
        constructor(sessionList: List<Value>) : this(
            arrayValue = ArrayValue(
                values = sessionList
            )
        )
    }

    @Serializable
    data class ArrayValue(
        val values: List<Value>,
    )

    @Serializable
    data class Value(
        val stringValue: String,
    )
}
