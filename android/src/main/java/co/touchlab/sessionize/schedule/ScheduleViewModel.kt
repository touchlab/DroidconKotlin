package co.touchlab.sessionize.schedule

import androidx.lifecycle.ViewModel
import co.touchlab.sessionize.ScheduleModel
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.display.DaySchedule

class ScheduleViewModel(allEvents: Boolean, sessionizeDbHelper: SessionizeDbHelper, timeZone: String) : ViewModel() {
    val scheduleModel = ScheduleModel(allEvents, sessionizeDbHelper, timeZone)

    fun registerForChanges(proc:(notes:List<DaySchedule>)->Unit){

        scheduleModel.register(object : ScheduleModel.ScheduleView {
            override suspend fun update(daySchedules: List<DaySchedule>) {
                proc(daySchedules)
            }
        })
    }

    fun unregister(){
        scheduleModel.shutDown()
    }
}