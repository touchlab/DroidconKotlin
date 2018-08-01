package co.touchlab.sessionize

import co.touchlab.sessionize.utils.backgroundTask

object AboutModel{
    fun loadAboutInfo(proc:(aboutInfo:List<AboutInfo>)->Unit){
        backgroundTask({
            AppContext.parseAbout()
        }, proc)
    }
}