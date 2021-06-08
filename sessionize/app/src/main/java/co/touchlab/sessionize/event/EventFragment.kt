package co.touchlab.sessionize.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import co.touchlab.sessionize.*
import co.touchlab.sessionize.databinding.FragmentEventBinding
import co.touchlab.sessionize.util.viewBindingLifecycle

class EventFragment : Fragment() {

    private var binding by viewBindingLifecycle<FragmentEventBinding>()

    val sessionId: String by lazy {
        arguments?.let {
            val eventArgs = EventFragmentArgs.fromBundle(it)
            eventArgs.sessionid
        } ?: ""
    }
    private val eventViewModel: EventViewModel by viewModels(factoryProducer = {
        EventViewModelFactory(sessionId)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false).apply {
            eventViewModel.eventModel.register(object : EventModel.EventView {
                override suspend fun update(data: SessionInfo) {
                    dataRefresh(data, data.session.formattedRoomTime())
                }
            })

            val adapter = EventDetailAdapter(activity!!)
            recycler.adapter = adapter
        }
        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        eventViewModel.eventModel.shutDown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun dataRefresh(eventInfo: SessionInfo, formattedRoomTime: String) {
        updateFAB(eventInfo)
        updateContent(eventInfo, formattedRoomTime)
    }

    @SuppressLint("RestrictedApi")
    private fun updateFAB(event: SessionInfo) {
        binding.run {
            fab.rippleColor = ContextCompat.getColor(requireContext(), android.R.color.black)

            if (event.isRsvped()) {
                fab.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check))
                fab.isActivated = true
            } else {
                fab.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_plus))
                fab.isActivated = false
            }

            val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
            if (event.isPast()) {
                fab.layoutParams = layoutParams
                fab.visibility = View.GONE
            } else {
                fab.setOnClickListener {

                    eventViewModel.eventModel.toggleRsvp(event)
                }

                fab.layoutParams = layoutParams
                fab.visibility = View.VISIBLE
            }
        }
    }

    private fun updateContent(event: SessionInfo, formattedRoomTime:String) {
        val adapter = EventDetailAdapter(requireActivity())

        binding.eventTitle.text = event.session.title
        binding.eventRoomTime.text = formattedRoomTime
        adapter.addHeader(event.session.title)

        when {
            event.isNow() -> adapter.addInfo("<i><b>" + resources.getString(R.string.event_now) + "</b></i>")
            event.isPast() -> adapter.addInfo("<i><b>" + resources.getString(R.string.event_past) + "</b></i>")
            event.conflict -> adapter.addInfo("<i><b>" + resources.getString(R.string.event_conflict) + "</b></i>")
        }

        if (!event.session.description.isBlank())
            adapter.addBody(event.session.description)

        for (item in event.speakers) {
            adapter.addSpeaker(item)
        }

        binding.recycler.adapter = adapter
    }

}