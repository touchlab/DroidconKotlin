package co.touchlab.notepad

import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.data.*
import co.touchlab.notepad.*
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