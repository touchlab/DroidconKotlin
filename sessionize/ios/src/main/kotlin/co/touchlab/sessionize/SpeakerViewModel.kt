package co.touchlab.sessionize

import co.touchlab.multiplatform.architecture.livedata.*
import co.touchlab.sessionize.platform.*
import co.touchlab.sessionize.*
import kotlin.native.*
import kotlin.native.concurrent.*

class SpeakerViewModel(sessionId: String){
    val speakerModel = SpeakerModel(sessionId).freeze()
    var speakerObserver:Observer<SpeakerUiData>? = null

    fun registerForChanges(proc:(speakerUiData:SpeakerUiData)->Unit){
        speakerObserver = object : Observer<SpeakerUiData>{
            override fun onChanged(t: SpeakerUiData?){
                if(t != null)
                    proc(t)
            }
        }

        speakerModel.uiLiveData().observeForever(speakerObserver!!)
    }

    fun unregister(){
        speakerModel.uiLiveData().removeObserver(speakerObserver!!)
        speakerObserver = null
        speakerModel.shutDown()
    }
}