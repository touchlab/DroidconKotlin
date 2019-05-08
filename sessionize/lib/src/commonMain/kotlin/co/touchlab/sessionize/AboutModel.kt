package co.touchlab.sessionize

import co.touchlab.sessionize.ServiceRegistry.clLogCallback
import co.touchlab.sessionize.ServiceRegistry.staticFileLoader
import co.touchlab.sessionize.platform.backgroundSuspend
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object AboutModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {
    fun loadAboutInfo(proc: (aboutInfo: List<AboutInfo>) -> Unit) = launch {
        clLogCallback("loadAboutInfo AboutModel()")
        proc(backgroundSuspend { AboutProc.parseAbout() })
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