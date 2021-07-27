package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SpeakerViewModel(sessionId: String): KoinComponent {
    val speakerModel = SpeakerModel(sessionId, get())

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