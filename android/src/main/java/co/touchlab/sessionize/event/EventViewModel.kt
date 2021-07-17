package co.touchlab.sessionize.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.EventModel

class EventViewModel(sessionId:String): ViewModel(){
    val eventModel = EventModel(sessionId)
}

class EventViewModelFactory(private val sessionId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventViewModel(sessionId) as T
    }
}