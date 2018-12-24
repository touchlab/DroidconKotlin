package co.touchlab.sessionize

import co.touchlab.sessionize.display.DaySchedule

class ScheduleViewModel(allEvents: Boolean){
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
}