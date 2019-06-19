package co.touchlab.sessionize.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.*
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
    lateinit var adapter: EventDetailAdapter
    lateinit var eventTitle: TextView
    lateinit var eventRoomTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(sessionId))[EventViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        fab = view.findViewById(R.id.fab)
        eventTitle = view.findViewById(R.id.eventTitle)
        eventRoomTime = view.findViewById(R.id.eventRoomTime)
        recycler = view.findViewById(R.id.recycler)

        eventViewModel.eventModel.register(object : EventModel.EventView {
            override suspend fun update(data: SessionInfo) {
                dataRefresh(data, data.session.formattedRoomTime())
            }
        })

        adapter = EventDetailAdapter(activity!!)
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
        eventTitle.text = event.session.title
        eventRoomTime.text = formattedRoomTime

        adapter.updateWithSession(event, resources)
        adapter.notifyDataSetChanged()
    }

}