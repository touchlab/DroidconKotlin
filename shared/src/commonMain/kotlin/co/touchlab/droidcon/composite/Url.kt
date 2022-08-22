package co.touchlab.droidcon.composite

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class Url(val string: String): Parcelable
