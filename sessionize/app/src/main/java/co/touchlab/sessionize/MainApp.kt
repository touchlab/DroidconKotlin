package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import co.touchlab.sessionize.platform.AndroidAppContext
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.answers.CustomEvent

class MainApp :Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.app = this
        Fabric.with(this, Answers())
        AppContext.initPlatformClient ({filePrefix, fileType ->
            loadAsset("${filePrefix}.${fileType}")},
                {name: String, params: Map<String, Any> ->

                    val event = CustomEvent(name)
                    for (key in params.keys) {
                        val obj = params.get(key)
                        when(obj){
                            is String -> event.putCustomAttribute(key, obj)
                            is Number -> event.putCustomAttribute(key, obj)
                            else -> {
                                throw IllegalArgumentException("Don't know what this is $key/$obj")
                            }
                        }
                    }

                    Answers.getInstance().logCustom(event)
                })

    }

    private fun loadAsset(name:String) = assets
            .open(name, Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }
}