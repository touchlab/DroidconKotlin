package co.touchlab.notepad.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.notepad.EventModel

class EventViewModel(sessionId:String): ViewModel(){
    val eventModel = EventModel(sessionId)
}

class EventViewModelFactory(private val sessionId: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventViewModel(sessionId) as T
    }
}