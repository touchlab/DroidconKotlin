package co.touchlab.sessionize

class SpeakerViewModel(sessionId: String){
    val speakerModel = SpeakerModel(sessionId)

    fun registerForChanges(proc:(speakerUiData:SpeakerUiData)->Unit){
        speakerModel.register(object : SpeakerModel.View{
            override fun update(speakerUiData: SpeakerUiData) {
                proc(speakerUiData)
            }

        })
    }

    fun unregister(){
        speakerModel.shutDown()
    }
}