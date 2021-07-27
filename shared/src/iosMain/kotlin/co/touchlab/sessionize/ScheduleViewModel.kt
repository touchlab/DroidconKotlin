package co.touchlab.sessionize

import co.touchlab.sessionize.display.DaySchedule
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ScheduleViewModel(allEvents: Boolean):KoinComponent {
    val scheduleModel = ScheduleModel(allEvents, get(), timeZone)

    fun registerForChanges(proc: (notes: List<DaySchedule>) -> Unit) {

        scheduleModel.register(object : ScheduleModel.ScheduleView {
            override suspend fun update(daySchedules: List<DaySchedule>) {
                proc(daySchedules)
            }
        })
    }

    fun unregister() {
        scheduleModel.shutDown()
    }
}

fun classname(a: Any): String? {
    return a::class.qualifiedName
}

