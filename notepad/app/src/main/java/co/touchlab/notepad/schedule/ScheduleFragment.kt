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
import co.touchlab.notepad.display.DaySchedule
import co.touchlab.notepad.event.EventFragment
import com.google.android.material.tabs.TabLayout

class ScheduleFragment:Fragment() {

    private val viewModel: ScheduleViewModel by lazy {
        ViewModelProviders.of(this)[ScheduleViewModel::class.java]
    }

    lateinit var dayChooser: TabLayout
    lateinit var eventList: RecyclerView
    lateinit var eventAdapter: EventAdapter

    var conferenceDays: List<DaySchedule> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel.scheduleModel.dayFormatLiveData(true).observe(viewLifecycleOwner,
                Observer {
                    conferenceDays = it
                    updateTabs(it)
                    updateDisplay()
                }
        )

        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        dayChooser = view.findViewById(R.id.dayChooser)
        eventList = view.findViewById(R.id.eventList)

        eventList.layoutManager = LinearLayoutManager(activity)

        eventAdapter = EventAdapter(context!!, viewModel.scheduleModel, true){
            (activity as NavigationHost).navigateTo(EventFragment.newInstance(it.timeBlock.id), true)
        }

        eventList.adapter = eventAdapter

        dayChooser.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
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
        val current = dayChooser.selectedTabPosition
        dayChooser.removeAllTabs()

        for (day in days) {
            dayChooser.addTab(dayChooser.newTab().setText(day.dayString))
        }

        if (current >= 0 && current < dayChooser.tabCount)
            dayChooser.getTabAt(current)?.select()
    }

    fun updateDisplay()
    {
        if(dayChooser.selectedTabPosition >= 0 && dayChooser.tabCount > dayChooser.selectedTabPosition) {
            eventAdapter.updateEvents(conferenceDays[dayChooser.selectedTabPosition].hourBlock)
        }
    }
}