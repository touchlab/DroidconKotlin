package co.touchlab.sessionize.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.ScheduleModel
import co.touchlab.sessionize.display.DaySchedule

class ScheduleViewModel(allEvents: Boolean) : ViewModel() {
    val scheduleModel = ScheduleModel(allEvents)

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

    class ScheduleViewModelFactory(private val allEvents: Boolean) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScheduleViewModel(allEvents) as T
        }
    }
}