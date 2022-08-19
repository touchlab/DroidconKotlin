package co.touchlab.droidcon.domain.entity

import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

expect object InstantParceler : Parceler<Instant>

expect object LocalDateParceler : Parceler<LocalDate>
