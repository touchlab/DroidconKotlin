package co.touchlab.sessionize

import co.touchlab.sessionize.display.DaySchedule

class ScheduleViewModel{
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