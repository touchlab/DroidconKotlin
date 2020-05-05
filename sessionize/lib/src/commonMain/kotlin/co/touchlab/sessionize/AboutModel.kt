package co.touchlab.sessionize

import co.touchlab.sessionize.ServiceRegistry.clLogCallback
import co.touchlab.sessionize.ServiceRegistry.staticFileLoader
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AboutModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {
    fun loadAboutInfo(proc: (aboutInfo: List<AboutInfo>) -> Unit) = mainScope.launch {
        clLogCallback("loadAboutInfo AboutModel()")
        proc(aboutLoad())
    }

    private suspend fun aboutLoad() = withContext(AppContext.backgroundContext){
        AboutProc.parseAbout()
    }
}

internal object AboutProc {
    fun parseAbout(): List<AboutInfo> {
        val aboutJsonString = staticFileLoader("about", "json")!!
        return Json.nonstrict.parse(AboutInfo.serializer().list, aboutJsonString)
    }
}

@Serializable
data class AboutInfo(val icon: String, val title: String, val detail: String)