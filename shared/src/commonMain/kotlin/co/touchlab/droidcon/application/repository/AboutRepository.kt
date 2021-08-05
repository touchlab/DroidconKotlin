package co.touchlab.droidcon.application.repository

import co.touchlab.droidcon.application.composite.AboutItem

interface AboutRepository {

    suspend fun getAboutItems(): List<AboutItem>
}