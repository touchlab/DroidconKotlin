package co.touchlab.sessionize.event

import android.app.Activity
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.EntryRecyclerAdapter
import co.touchlab.sessionize.SessionInfo
import co.touchlab.sessionize.isNow
import co.touchlab.sessionize.isPast

class EventDetailAdapter(private val activity: Activity) : EntryRecyclerAdapter(activity) {

    fun updateWithSession(session: SessionInfo, resources: Resources){
        removeAll()
        addHeader(session.session.title)
        when {
            session.isNow() -> addInfo("<i><b>" + resources.getString(R.string.event_now) + "</b></i>")
            session.isPast() -> addInfo("<i><b>" + resources.getString(R.string.event_past) + "</b></i>")
            session.conflict -> addInfo("<i><b>" + resources.getString(R.string.event_conflict) + "</b></i>")
        }

        if (!session.session.description.isBlank())
            addBody(session.session.description)

        for (item in session.speakers) {
            addSpeaker(item)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EntryType.values()[viewType]) {
            EntryType.TYPE_HEADER -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_event_header, parent, false)
                HeaderVH(view)
            }
            EntryType.TYPE_BODY -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_event_text, parent, false)
                TextVH(view)
            }
            EntryType.TYPE_INFO -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_event_info, parent, false)
                InfoVH(view)
            }
            EntryType.TYPE_SPEAKER -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_speaker_summary, parent, false)
                SpeakerVH(view)
            }
            else -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_event_text, parent, false)
                TextVH(view)
            }
        }
    }
}