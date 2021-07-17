package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount

class SpeakerViewModel(sessionId: String) {
    val speakerModel = SpeakerModel(sessionId)

    fun registerForChanges(proc: (speakerUiData: SpeakerUiData) -> Unit) {
        speakerModel.register(object : SpeakerModel.SpeakerView {
            override suspend fun update(data: UserAccount) {
                proc(speakerModel.speakerUiData(data))
            }
        })
    }

    fun unregister() {
        speakerModel.shutDown()
    }
}