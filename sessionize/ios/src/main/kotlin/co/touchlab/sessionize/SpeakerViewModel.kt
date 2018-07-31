package co.touchlab.sessionize

import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.sessionize.data.*
import co.touchlab.sessionize.*
import konan.worker.*

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