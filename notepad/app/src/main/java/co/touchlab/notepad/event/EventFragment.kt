package co.touchlab.notepad.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.notepad.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EventFragment : Fragment() {
    companion object {
        val SESSION_ID = "sessionId"

        fun newInstance(sessionId: String): EventFragment {
            return EventFragment().apply {
                arguments = Bundle().apply {
                    putString(SESSION_ID, sessionId)
                }
            }
        }
    }

    val sessionId: String by lazy { arguments?.getString(SESSION_ID) ?: "" }
    lateinit var eventViewModel: EventViewModel
    lateinit var fab: FloatingActionButton
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(sessionId))[EventViewModel::class.java]
        eventViewModel.eventModel.evenLiveData.observe(this, Observer {
            dataRefresh(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        fab = view.findViewById(R.id.fab)
        recycler = view.findViewById(R.id.recycler)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
//        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(getActivity())

    }

    private fun dataRefresh(eventInfo: SessionInfo) {
        updateFAB(eventInfo)
        updateContent(eventInfo)
    }

    private fun updateFAB(event: SessionInfo) {
        fab.rippleColor = ContextCompat.getColor(context!!, R.color.white)

        if (event.isRsvped()) {
            fab.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_check))
            fab.isActivated = true
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_plus))
            fab.isActivated = false
        }

        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        if (event.isPast()) {
            layoutParams.anchorId = View.NO_ID
            fab.layoutParams = layoutParams
            fab.visibility = View.GONE
        } else {
            fab.setOnClickListener {
                eventViewModel.eventModel.toggleRsvp(!event.isRsvped())
            }

            layoutParams.anchorId = R.id.appbar
            fab.layoutParams = layoutParams
            fab.visibility = View.VISIBLE
        }
    }

    private fun updateContent(event: SessionInfo) {
        val adapter = EventDetailAdapter(activity!!)

        adapter.addHeader(event.session.title, event.formattedRoomTime())

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