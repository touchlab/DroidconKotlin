package co.touchlab.droidcon.domain.entity

import android.os.Parcel
import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

actual object InstantParceler: Parceler<Instant> {

    override fun create(parcel: Parcel): Instant =
        Instant.fromEpochSeconds(parcel.readLong())

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(epochSeconds)
    }
}

actual object LocalDateParceler: Parceler<LocalDate> {

    override fun create(parcel: Parcel): LocalDate =
        LocalDate.parse(parcel.readString()!!)

    override fun LocalDate.write(parcel: Parcel, flags: Int) {
        parcel.writeString(toString())
    }
}
