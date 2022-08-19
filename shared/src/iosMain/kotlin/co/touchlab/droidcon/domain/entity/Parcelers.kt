package co.touchlab.droidcon.domain.entity

import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

actual object InstantParceler : Parceler<Instant>

actual object LocalDateParceler : Parceler<LocalDate>
