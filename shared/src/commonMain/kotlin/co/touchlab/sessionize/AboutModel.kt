package co.touchlab.sessionize

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AboutModel : BaseModel() {
    private val clLogCallback: ClLogCallback by inject(qualifier = named("clLog"))
    fun loadAboutInfo(proc: (aboutInfo: List<AboutInfo>) -> Unit) = mainScope.launch {
        clLogCallback("loadAboutInfo AboutModel()")
        proc(aboutLoad())
    }

    private suspend fun aboutLoad() = withContext(backgroundDispatcher) {
        AboutProc.parseAbout()
    }
}

@ThreadLocal
private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}

internal object AboutProc : KoinComponent {
    fun parseAbout(): List<AboutInfo> {
        val aboutJsonString = staticFileLoader("about", "json")!!
        return json.decodeFromString(ListSerializer(AboutInfo.serializer()), aboutJsonString)
    }
}

@Serializable
data class AboutInfo(val icon: String, val title: String, val detail: String)