package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.livedata.*
import co.touchlab.sessionize.display.DaySchedule
import kotlin.native.*
import kotlin.native.concurrent.*

class ScheduleViewModel{

    val scheduleModel = ScheduleModel().freeze()

    var scheduleObserver:Observer<List<DaySchedule>>? = null

    var liveData:MutableLiveData<List<DaySchedule>>? = null

    fun registerForChanges(proc:(notes:List<DaySchedule>)->Unit, allEvents:Boolean){

        scheduleObserver = object : Observer<List<DaySchedule>>{
            override fun onChanged(t: List<DaySchedule>?){
                if(t != null)
                    proc(t)
            }
        }

        liveData = scheduleModel.dayFormatLiveData(allEvents)
        liveData!!.observeForever(scheduleObserver!!)
    }

    fun unregister(){
        liveData!!.removeObserver(scheduleObserver!!)
        liveData = null
        scheduleObserver = null
        scheduleModel.shutDown()
    }
}