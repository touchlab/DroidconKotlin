package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.data.*
import co.touchlab.notepad.display.DaySchedule
import konan.worker.*

class NotesViewModel{

    val noteModel = NoteModel().freeze()
    var notesObserver:Observer<List<DaySchedule>>? = null

    fun registerForChanges(proc:(notes:List<DaySchedule>)->Unit){

        notesObserver = object : Observer<List<DaySchedule>>{
            override fun onChanged(t: List<DaySchedule>?){
                for(ds in t!!){
                    println("ds: $ds")
                }
                if(t != null)
                    proc(t)
            }
        }

        noteModel.dayFormatLiveData().observeForever(notesObserver!!)
    }

    fun unregister(){
        noteModel.dayFormatLiveData().removeObserver(notesObserver!!)
        notesObserver = null
        noteModel.shutDown()
    }

    fun primeData(speakerJson:String, scheduleJson:String){
        noteModel.primeData(speakerJson, scheduleJson)
    }

    fun insertNote(title:String, description:String){
        noteModel.insertNote(title, description)
    }
}