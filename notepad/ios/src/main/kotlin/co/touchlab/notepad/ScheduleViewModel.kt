package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.data.*
import co.touchlab.notepad.display.DaySchedule
import konan.worker.*

class ScheduleViewModel{
    val scheduleModel = ScheduleModel().freeze()
    var scheduleObserver:Observer<List<DaySchedule>>? = null

    fun registerForChanges(proc:(notes:List<DaySchedule>)->Unit){

        scheduleObserver = object : Observer<List<DaySchedule>>{
            override fun onChanged(t: List<DaySchedule>?){
                if(t != null)
                    proc(t)
            }
        }

        scheduleModel.dayFormatLiveData().observeForever(scheduleObserver!!)
    }

    fun unregister(){
        scheduleModel.dayFormatLiveData().removeObserver(scheduleObserver!!)
        scheduleObserver = null
        scheduleModel.shutDown()
    }
}