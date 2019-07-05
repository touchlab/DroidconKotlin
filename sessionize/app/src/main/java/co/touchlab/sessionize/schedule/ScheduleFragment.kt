package co.touchlab.sessionize.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.FragmentAnimation
import co.touchlab.sessionize.NavigationHost
import co.touchlab.sessionize.R
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.event.EventFragment
import com.google.android.material.tabs.TabLayout

class ScheduleFragment:Fragment() {

    private val viewModel: ScheduleViewModel by lazy {
        ViewModelProviders.of(this, ScheduleViewModel.ScheduleViewModelFactory(allEvents))[ScheduleViewModel::class.java]
    }

    val allEvents: Boolean by lazy {
        arguments?.let {
            val scheduleArgs = ScheduleFragmentArgs.fromBundle(it)
            scheduleArgs.allevents
        } ?: true
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
            updateDisplay()
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

        val current = dayChooser.selectedTabPosition
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

            if (current >= 0 && current < dayChooser.tabCount)
                dayChooser.getTabAt(current)?.select()
        }
    }

    fun updateDisplay()
    {
        if(dayChooser.selectedTabPosition >= 0 && dayChooser.tabCount > dayChooser.selectedTabPosition) {
            eventAdapter.updateEvents(conferenceDays[dayChooser.selectedTabPosition].hourBlock)
        }
    }
}