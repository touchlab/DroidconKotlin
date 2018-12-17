package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount
import co.touchlab.multiplatform.architecture.livedata.Observer
import kotlin.native.concurrent.freeze

class SpeakerViewModel(sessionId: String){
    val speakerModel = SpeakerModel(sessionId).freeze()
    var speakerObserver:Observer<UserAccount>? = null

    fun registerForChanges(proc:(speakerUiData:SpeakerUiData)->Unit){
        speakerObserver = object : Observer<UserAccount>{
            override fun onChanged(t: UserAccount?){
                if(t != null)
                    speakerModel.processUser(t, proc)
            }
        }

        speakerModel.speakerLiveData.observeForever(speakerObserver!!)
    }

    fun unregister(){
        speakerModel.speakerLiveData.removeObserver(speakerObserver!!)
        speakerObserver = null
        speakerModel.shutDown()
    }
}