package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.AppContext.dbHelper
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.display.*
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel(private val allEvents: Boolean) : BaseQueryModelView<SessionWithRoom, List<DaySchedule>>(
        dbHelper.getSessionsQuery(),
        {
            val dbSessions = it.executeAsList()
            val sessions = if(allEvents){dbSessions}else{dbSessions.filter {it.rsvp != 0L}}
            val ourGuy = sessions.findLast {
                it.id.equals("eea05a06-92d6-46bb-bec6-69198174aa5d")
            }

            val strings = Array(200) {
                "Hello $it"
            }

            val bools = Array(200) {
                it % 2 == 0
            }

            val bytes = ByteArray(200) {
                it.toByte()
            }

            val enums = Array(200) {
                RsvpState.values()[it % RsvpState.values().size]
            }

            val bigClasses = Array(200) {
                Lots(
                        "a",
                        "b",
                        "c",
                        "d",
                        "e",
                        "f",
                        "g",
                        "h",
                        "i",
                        "j",
                        "k",
                        "l",
                        "m",
                        "n",
                        "o",
                        "p",
                        "q",
                        "r",
                        "s",
                        "t",
                        "u",
                        "v",
                        "w",
                        "x",
                        "y",
                        "z",
                        "aa",
                        "ab",
                        "ac",
                        "ad",
                        "ae",
                        "af",
                        "ag",
                        "ah",
                        "ai",
                        "aj",
                        "ak",
                        "al",
                        "am",
                        "an",
                        "ao",
                        "ap",
                        "aq",
                        "ar",
                        "at",
                        "au",
                        "av",
                        "aw",
                        "ax",
                        "ay",
                        "az",
                        "ba",
                        "bb",
                        "bc",
                        "bd",
                        "be",
                        "bf",
                        "bg",
                        "bh",
                        "bi",
                        "bj",
                        "bk",
                        "bl",
                        "bm",
                        "bn",
                        "bo",
                        "bp",
                        "bq",
                        "br",
                        "bs",
                        "bt",
                        "bu",
                        "bv",
                        "bw",
                        "bx",
                        "by",
                        "bz",
                        "ca",
                        "cb",
                        "cc",
                        "cd",
                        "ce",
                        "cf",
                        "cg",
                        "ch",
                        "ci",
                        "cj",
                        "ck",
                        "cl",
                        "cm",
                        "cn",
                        "co",
                        "cp",
                        "cq",
                        "cr",
                        "cs",
                        "ct",
                        "cu",
                        "cv",
                        "cw",
                        "cx",
                        "cy",
                        "cz"
                )
            }

            val stringList = mutableListOf("a", "b", "c")

            print("I like strings ${strings.size}")
            println("OUR GUY: "+ ourGuy)
            val hourBlocks = formatHourBlocks(sessions)
            convertMapToDaySchedule(hourBlocks).freeze() //TODO: This shouldn't need to be frozen
            //Spent several full days trying to debug why, but haven't sorted it out.
        },
        AppContext.dispatcherLocal.lateValue) {

    init {
        clLog("init ScheduleModel()")
        ensureNeverFrozen()
    }

    fun register(view:ScheduleView){
        super.register(view)
    }

    interface ScheduleView:View<List<DaySchedule>>

    fun weaveSessionDetailsUi(hourBlock:HourBlock, allBlocks:List<HourBlock>, row:EventRow, allEvents: Boolean){
        val isFirstInBlock = !hourBlock.hourStringDisplay.isEmpty()
        row.setTimeGap(isFirstInBlock)

        row.setTitleText(hourBlock.timeBlock.title)
        row.setTimeText(hourBlock.hourStringDisplay)
        val speakerNames = if(hourBlock.timeBlock.allNames.isNullOrBlank()){""}else{hourBlock.timeBlock.allNames!!}
        row.setSpeakerText(speakerNames)
        row.setDescription(hourBlock.timeBlock.description)

        if (hourBlock.timeBlock.isBlock()) {
            row.setLiveNowVisible(false)
            row.setRsvpState(RsvpState.None)
        } else {
            //TODO: Add live
            row.setLiveNowVisible(false)

            val rsvpShow = allEvents && hourBlock.timeBlock.isRsvp()
            val state = if(rsvpShow){
                if(hourBlock.isPast()) {
                    RsvpState.RsvpPast
                }else{
                    if(hourBlock.isConflict(allBlocks)){
                        RsvpState.Conflict
                    }
                    else{
                        RsvpState.Rsvp
                    }
                }
            }else{
                RsvpState.None
            }
            row.setRsvpState(state)
        }
    }
}

enum class RsvpState {
    None, Rsvp, Conflict, RsvpPast
}

interface EventRow {
    fun setTimeGap(b: Boolean)

    fun setTitleText(s: String)

    fun setTimeText(s: String)

    fun setSpeakerText(s: String)

    fun setDescription(s: String)

    fun setLiveNowVisible(b: Boolean)

    fun setRsvpState(state:RsvpState)
}

data class Lots(
        val a:String,
        val b:String,
        val c:String,
        val d:String,
        val e:String,
        val f:String,
        val g:String,
        val h:String,
        val i:String,
        val j:String,
        val k:String,
        val l:String,
        val m:String,
        val n:String,
        val o:String,
        val p:String,
        val q:String,
        val r:String,
        val s:String,
        val t:String,
        val u:String,
        val v:String,
        val w:String,
        val x:String,
        val y:String,
        val z:String,
        val aa:String,
        val ab:String,
        val ac:String,
        val ad:String,
        val ae:String,
        val af:String,
        val ag:String,
        val ah:String,
        val ai:String,
        val aj:String,
        val ak:String,
        val al:String,
        val am:String,
        val an:String,
        val ao:String,
        val ap:String,
        val aq:String,
        val ar:String,
        val at:String,
        val au:String,
        val av:String,
        val aw:String,
        val ax:String,
        val ay:String,
        val az:String,
        val ba:String,
        val bb:String,
        val bc:String,
        val bd:String,
        val be:String,
        val bf:String,
        val bg:String,
        val bh:String,
        val bi:String,
        val bj:String,
        val bk:String,
        val bl:String,
        val bm:String,
        val bn:String,
        val bo:String,
        val bp:String,
        val bq:String,
        val br:String,
        val bs:String,
        val bt:String,
        val bu:String,
        val bv:String,
        val bw:String,
        val bx:String,
        val by:String,
        val bz:String,
        val ca:String,
        val cb:String,
        val cc:String,
        val cd:String,
        val ce:String,
        val cf:String,
        val cg:String,
        val ch:String,
        val ci:String,
        val cj:String,
        val ck:String,
        val cl:String,
        val cm:String,
        val cn:String,
        val co:String,
        val cp:String,
        val cq:String,
        val cr:String,
        val cs:String,
        val ct:String,
        val cu:String,
        val cv:String,
        val cw:String,
        val cx:String,
        val cy:String,
        val cz:String
)