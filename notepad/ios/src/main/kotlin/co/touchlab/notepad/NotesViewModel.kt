package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.data.*
import konan.worker.*

class NotesViewModel{

    val noteModel = NoteModel().freeze()
    var notesObserver:Observer<List<SessionWithRoom>>? = null

    fun registerForChanges(proc:(notes:List<SessionWithRoom>)->Unit){

        noteModel.primeData()

        notesObserver = object : Observer<List<SessionWithRoom>>{
            override fun onChanged(t: List<SessionWithRoom>?){
                if(t != null)
                    proc(t)
            }
        }

        noteModel.sessionsLiveData().observeForever(notesObserver!!)
    }

    fun unregister(){
        noteModel.sessionsLiveData().removeObserver(notesObserver!!)
        notesObserver = null
    }

    fun insertNote(title:String, description:String){
        noteModel.insertNote(title, description)
    }
}