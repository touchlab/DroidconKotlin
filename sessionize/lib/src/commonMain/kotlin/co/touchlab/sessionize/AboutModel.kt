package co.touchlab.sessionize

import co.touchlab.sessionize.platform.backgroundTask
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

object AboutModel{
    fun loadAboutInfo(proc:(aboutInfo:List<AboutInfo>)->Unit){
        clLog("loadAboutInfo AboutModel()")
        backgroundTask({
            parseAbout()
        }, proc)
    }

    private fun parseAbout():List<AboutInfo>{
        val aboutJsonString = AppContext.staticFileLoader("about", "json")!!
        val aboutList = ArrayList<AboutInfo>()

        val json = JsonTreeParser(aboutJsonString).readFully()

        (json as JsonArray).forEach {
            val aboutJson = it as JsonObject
            aboutList.add(
                    AboutInfo(
                            aboutJson.getPrimitive("icon").content,
                            aboutJson.getPrimitive("title").content,
                            aboutJson.getPrimitive("detail").content
                    )
            )
        }

        return aboutList
    }
}

data class AboutInfo(val icon:String, val title:String, val detail:String)