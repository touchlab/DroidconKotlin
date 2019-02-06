package co.touchlab.sessionize

import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.platform.currentTimeMillis

class ScheduleViewModel(allEvents: Boolean){
    init {
        xcodeKotlinisActive()
        if(currentTimeMillis() < 1234) {
            xcodeKotlinClassName()
            xcodeKotlinDisposeString()
            xcodeKotlinArrayType()
            xcodeKotlinArrayBase()
            xcodeKotlinArrayTypeSize()
        }
    }
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

fun classname(a:Any):String?{
    return a::class.qualifiedName
}

@SymbolName("XcodeKotlin_isActive")
external fun xcodeKotlinisActive():Int

@SymbolName("XcodeKotlin_className")
external fun xcodeKotlinClassName()

@SymbolName("XcodeKotlin_disposeString")
external fun xcodeKotlinDisposeString()

@SymbolName("XcodeKotlin_arrayType")
external fun xcodeKotlinArrayType()

@SymbolName("XcodeKotlin_arrayBase")
external fun xcodeKotlinArrayBase()

@SymbolName("XcodeKotlin_arrayTypeSize")
external fun xcodeKotlinArrayTypeSize()


