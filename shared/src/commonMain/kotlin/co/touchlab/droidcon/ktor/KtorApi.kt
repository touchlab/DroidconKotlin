package co.touchlab.droidcon.ktor

import co.touchlab.droidcon.response.BreedResult

interface KtorApi {
    suspend fun getJsonFromApi(): BreedResult
}
