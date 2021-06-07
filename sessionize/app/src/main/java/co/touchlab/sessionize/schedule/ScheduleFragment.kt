package co.touchlab.sessionize.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.touchlab.sessionize.MainActivity
import co.touchlab.sessionize.databinding.FragmentScheduleBinding
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.util.viewBindingLifecycle
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment:Fragment() {

    private var binding by viewBindingLifecycle<FragmentScheduleBinding>()

    private val viewModel: ScheduleViewModel by viewModels(factoryProducer = {
        ScheduleViewModel.ScheduleViewModelFactory(allEvents)
    })

    val allEvents: Boolean by lazy {
        arguments?.let {
            val scheduleArgs = ScheduleFragmentArgs.fromBundle(it)
            scheduleArgs.allevents
        } ?: true
    }
    lateinit var eventAdapter: EventAdapter

    var conferenceDays: List<DaySchedule> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)

        viewModel.registerForChanges {days:List<DaySchedule> ->
            conferenceDays = days
            updateTabs(days)


            (activity as? MainActivity)?.let {
                if (allEvents) {
                    binding.dayChooser.getTabAt(it.scheduleTabPos)?.select()
                    updateDisplay()
                    if (it.scheduleRecyclerViewPos == null) {
                        defaultToCurrentDay()
                    } else {
                        binding.eventList.layoutManager?.onRestoreInstanceState(it.scheduleRecyclerViewPos)
                    }
                } else {
                    binding.dayChooser.getTabAt(it.agendaTabPos)?.select()
                    updateDisplay()
                    binding.eventList.layoutManager?.onRestoreInstanceState(it.agendaRecyclerViewPos)
                }
            }
        }

        binding.eventList.layoutManager = LinearLayoutManager(activity)

        eventAdapter = EventAdapter(requireContext(), allEvents) {
            val direction = ScheduleFragmentDirections.actionScheduleFragmentToEventFragment(it.timeBlock.id)
            binding.root.findNavController().navigate(direction)
            //navigateToSession(it.timeBlock.id)
        }

        binding.eventList.adapter = eventAdapter

        binding.dayChooser.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                binding.eventList.scrollToPosition(0)
                updateDisplay()
            }
        })

        return binding.root
    }

    private fun defaultToCurrentDay() {
        val currentDayString = SimpleDateFormat("MMM dd", Locale.US).format(Date())
        val dayStringList = conferenceDays.map { it.dayString }

        dayStringList.forEachIndexed { index, dayString ->
            if (dayString == currentDayString) {
                binding.dayChooser.getTabAt(index)?.select()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if(activity is MainActivity){
            val test = activity as MainActivity
            if(allEvents){
                test.scheduleRecyclerViewPos = binding.eventList.layoutManager?.onSaveInstanceState()
                test.scheduleTabPos = binding.dayChooser.selectedTabPosition

            }
            else{
                test.agendaRecyclerViewPos = binding.eventList.layoutManager?.onSaveInstanceState()
                test.agendaTabPos = binding.dayChooser.selectedTabPosition

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
        if(days.isNotEmpty() && binding.dayChooser.tabCount == days.size)
            return

        binding.dayChooser.removeAllTabs()

        if(days.size == 0)
        {
            binding.dayChooser.visibility = View.GONE
            binding.eventList.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
        else {
            binding.dayChooser.visibility = View.VISIBLE
            binding.eventList.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE

            for (day in days) {
                binding.dayChooser.addTab(binding.dayChooser.newTab().setText(day.dayString))
            }
        }
    }

    fun updateDisplay()
    {
        if(binding.dayChooser.selectedTabPosition >= 0 && binding.dayChooser.tabCount > binding.dayChooser.selectedTabPosition) {
            eventAdapter.updateEvents(conferenceDays[binding.dayChooser.selectedTabPosition].hourBlock)
        }
    }
}
