package co.touchlab.sessionize.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.MainActivity
import co.touchlab.sessionize.R
import co.touchlab.sessionize.display.DaySchedule
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment:Fragment() {
    val allEvents: Boolean by lazy {
        arguments?.let {
            val scheduleArgs = ScheduleFragmentArgs.fromBundle(it)
            scheduleArgs.allevents
        } ?: true
    }

    private val viewModel: ScheduleViewModel by viewModel {
        parametersOf(allEvents)
    }

    lateinit var dayChooser: TabLayout
    lateinit var eventList: RecyclerView
    lateinit var noDataText: TextView
    lateinit var eventAdapter: EventAdapter

    var conferenceDays: List<DaySchedule> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel.registerForChanges {days:List<DaySchedule> ->
            conferenceDays = days
            updateTabs(days)


            (activity as? MainActivity)?.let {
                if (allEvents) {
                    dayChooser.getTabAt(it.scheduleTabPos)?.select()
                    updateDisplay()
                    if (it.scheduleRecyclerViewPos == null) {
                        defaultToCurrentDay()
                    } else {
                        eventList.layoutManager?.onRestoreInstanceState(it.scheduleRecyclerViewPos)
                    }
                } else {
                    dayChooser.getTabAt(it.agendaTabPos)?.select()
                    updateDisplay()
                    eventList.layoutManager?.onRestoreInstanceState(it.agendaRecyclerViewPos)
                }
            }
        }

        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        dayChooser = view.findViewById(R.id.dayChooser)
        eventList = view.findViewById(R.id.eventList)
        noDataText = view.findViewById(R.id.noData)

        eventList.layoutManager = LinearLayoutManager(activity)

        eventAdapter = EventAdapter(context!!, allEvents) {
            val direction = ScheduleFragmentDirections.actionScheduleFragmentToEventFragment(it.timeBlock.id)
            view.findNavController().navigate(direction)
            //navigateToSession(it.timeBlock.id)
        }

        eventList.adapter = eventAdapter

        dayChooser.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                eventList.scrollToPosition(0)
                updateDisplay()
            }
        })

        return view
    }

    private fun defaultToCurrentDay() {
        val currentDayString = SimpleDateFormat("MMM dd", Locale.US).format(Date())
        val dayStringList = conferenceDays.map { it.dayString }

        dayStringList.forEachIndexed { index, dayString ->
            if (dayString == currentDayString) {
                dayChooser.getTabAt(index)?.select()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if(activity is MainActivity){
            val test = activity as MainActivity
            if(allEvents){
                test.scheduleRecyclerViewPos = eventList.layoutManager?.onSaveInstanceState()
                test.scheduleTabPos = dayChooser.selectedTabPosition

            }
            else{
                test.agendaRecyclerViewPos = eventList.layoutManager?.onSaveInstanceState()
                test.agendaTabPos = dayChooser.selectedTabPosition

            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        viewModel.unregister()
    }

    fun updateTabs(days: List<DaySchedule>) {

        //We're doing a slightly lazy compare here. If you change the days of your conference,
        //but do not change the number of days, if the user is on screen when it happens, the
        //dates won't refresh right away. Assume we can live with it.
        if(days.isNotEmpty() && dayChooser.tabCount == days.size)
            return

        dayChooser.removeAllTabs()

        if(days.size == 0)
        {
            dayChooser.visibility = View.GONE
            eventList.visibility = View.GONE
            noDataText.visibility = View.VISIBLE
        }
        else {
            dayChooser.visibility = View.VISIBLE
            eventList.visibility = View.VISIBLE
            noDataText.visibility = View.GONE

            for (day in days) {
                dayChooser.addTab(dayChooser.newTab().setText(day.dayString))
            }
        }
    }

    fun updateDisplay()
    {
        if(dayChooser.selectedTabPosition >= 0 && dayChooser.tabCount > dayChooser.selectedTabPosition) {
            eventAdapter.updateEvents(conferenceDays[dayChooser.selectedTabPosition].hourBlock)
        }
    }
}
