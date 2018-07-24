package co.touchlab.notepad

import android.app.Application
import android.content.Context
import co.touchlab.notepad.utils.AndroidAppContext

class MainApp :Application(){

    override fun onCreate() {
        super.onCreate()

        AndroidAppContext.app = this

        AppContext.primeData(
                speakerJson = loadAsset("speakers.json"),
                scheduleJson = loadAsset("schedule.json")
        )
    }

    private fun loadAsset(name:String) = assets
            .open(name, Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }

}