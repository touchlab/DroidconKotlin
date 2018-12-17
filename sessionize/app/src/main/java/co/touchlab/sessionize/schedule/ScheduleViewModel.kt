package co.touchlab.sessionize.schedule

import androidx.lifecycle.ViewModel
import co.touchlab.sessionize.ScheduleModel
import co.touchlab.sessionize.display.DaySchedule

class ScheduleViewModel : ViewModel() {
    val scheduleModel = ScheduleModel()

    fun registerForChanges(allEvents:Boolean, proc:(notes:List<DaySchedule>)->Unit){

        scheduleModel.register(allEvents, object : ScheduleModel.ScheduleView {
            override fun update(daySchedules: List<DaySchedule>) {
                proc(daySchedules)
            }
        })
    }

    fun unregister(){
        scheduleModel.shutDown()
    }
}