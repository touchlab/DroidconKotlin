package co.touchlab.sessionize.api

import co.touchlab.sessionize.jsondata.Speaker
import io.ktor.client.features.json.serializer.*

object ApiSupport{
    val serializer = KotlinxSerializer().apply {
        setMapper(Speaker::class, Speaker.serializer())
//                setMapper(Favorite::class, Favorite.serializer())
//                setMapper(Vote::class, Vote.serializer())
    }
}
/*
class SessionizeApi(private val endPoint: String){
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(Speaker::class, Speaker.serializer())
//                setMapper(Favorite::class, Favorite.serializer())
//                setMapper(Vote::class, Vote.serializer())
            }
        }
        install(ExpectSuccess)
    }

    */
/*suspend fun createUser(userId: String): Boolean = client.request<HttpResponse> {
        apiUrl("users")
        method = HttpMethod.Post
        body = userId
    }.use {
        it.status.isSuccess()
    }*//*


    suspend fun getSpeakers(userId: String?): List<Speaker> = client.get {
        apiUrl("all", userId)
    }
}*/
