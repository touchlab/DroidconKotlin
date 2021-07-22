package co.touchlab.sessionize.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.*
import co.touchlab.sessionize.db.SessionizeDbHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class EventFragment : Fragment() {

    val timeZone: String by inject(qualifier = named("timeZone"))
    val dbHelper: SessionizeDbHelper by inject()
    val sessionId: String by lazy {
        arguments?.let {
            val eventArgs = EventFragmentArgs.fromBundle(it)
            eventArgs.sessionid
        } ?: ""
    }
    private val eventViewModel: EventViewModel by viewModel {
        parametersOf(sessionId)
    }
    
    lateinit var fab: FloatingActionButton
    lateinit var recycler: RecyclerView
    lateinit var eventTitle: TextView
    lateinit var eventRoomTime: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        fab = view.findViewById(R.id.fab)
        eventTitle = view.findViewById(R.id.eventTitle)
        eventRoomTime = view.findViewById(R.id.eventRoomTime)
        recycler = view.findViewById(R.id.recycler)

        eventViewModel.eventModel.register(object : EventModel.EventView {
            override suspend fun update(data: SessionInfo) {
                dataRefresh(data, data.session.formattedRoomTime(SessionRoomtimeFormatter(timeZone), dbHelper))
            }
        })

        val adapter = EventDetailAdapter(requireContext())
        recycler.adapter = adapter

        return view
    }

    override fun onDestroyView(){
        super.onDestroyView()
        eventViewModel.eventModel.shutDown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(getActivity())
    }

    private fun dataRefresh(eventInfo: SessionInfo, formattedRoomTime: String) {
        updateFAB(eventInfo)
        updateContent(eventInfo, formattedRoomTime)
    }

    @SuppressLint("RestrictedApi")
    private fun updateFAB(event: SessionInfo) {
        fab.rippleColor = ContextCompat.getColor(requireContext(), R.color.black)

        if (event.isRsvped()) {
            fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check))
            fab.isActivated = true
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus))
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

    private fun updateContent(event: SessionInfo, formattedRoomTime:String) {
        val adapter = EventDetailAdapter(requireContext())

        eventTitle.text = event.session.title
        eventRoomTime.text = formattedRoomTime
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

        recycler.adapter = adapter
    }

}