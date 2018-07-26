package co.touchlab.notepad

import android.app.Application
import android.content.Context
import co.touchlab.notepad.utils.AndroidAppContext

class MainApp :Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.app = this
        AppContext.initPlatformClient {filePrefix, fileType ->
            loadAsset("${filePrefix}.${fileType}")}
    }

    private fun loadAsset(name:String) = assets
            .open(name, Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }
}