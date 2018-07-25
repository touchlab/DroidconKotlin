package co.touchlab.notepad.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.notepad.NavigationHost
import co.touchlab.notepad.R
import co.touchlab.notepad.ScheduleModel
import co.touchlab.notepad.display.DaySchedule
import co.touchlab.notepad.display.HourBlock
import co.touchlab.notepad.event.EventFragment
import com.google.android.material.tabs.TabLayout

class ScheduleFragment:Fragment() {

    private val viewModel: ScheduleViewModel by lazy {
        ViewModelProviders.of(this)[ScheduleViewModel::class.java]
    }

    lateinit var scheduleDaysTab: TabLayout
    lateinit var eventList: RecyclerView
    lateinit var eventAdapter: EventAdapter
    var days: List<DaySchedule> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel.scheduleModel.dayFormatLiveData(true).observe(viewLifecycleOwner,
                Observer {
                    days = it
                    updateTabs(it)
                    updateDisplay()
                }
        )

        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        scheduleDaysTab = view.findViewById(R.id.scheduleDaysTab)
        eventList = view.findViewById(R.id.eventList)

        eventList.layoutManager = LinearLayoutManager(activity)

        eventAdapter = EventAdapter(context!!, viewModel.scheduleModel, true){
            (activity as NavigationHost).navigateTo(EventFragment.newInstance(it.timeBlock.id), true)
        }

        eventList.adapter = eventAdapter

        scheduleDaysTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                updateDisplay()
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        println("onResume called")
    }

    fun updateTabs(days: List<DaySchedule>) {
        val current = scheduleDaysTab.selectedTabPosition
        scheduleDaysTab.removeAllTabs()

        for (day in days) {
            scheduleDaysTab.addTab(scheduleDaysTab.newTab().setText(day.dayString))
        }

        if (current >= 0 && current < scheduleDaysTab.tabCount)
            scheduleDaysTab.getTabAt(current)?.select()
    }

    fun updateDisplay()
    {
        if(scheduleDaysTab.selectedTabPosition >= 0 && scheduleDaysTab.tabCount > scheduleDaysTab.selectedTabPosition) {
            eventAdapter.updateEvents(days[scheduleDaysTab.selectedTabPosition].hourBlock)
        }
    }

    inner class DaySelectedListener: TabLayout.OnTabSelectedListener{
        override fun onTabReselected(p0: TabLayout.Tab?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}