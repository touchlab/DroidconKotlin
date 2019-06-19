package co.touchlab.sessionize.sponsors

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.EntryRecyclerAdapter
import co.touchlab.sessionize.SponsorSessionInfo

class SponsorSessionAdapter(private val activity: Activity) : EntryRecyclerAdapter(activity) {


    fun updateWithSponsor(sponsorInfo: SponsorSessionInfo){
        removeAll()

        val sponsor = sponsorInfo.sponsor

        addHeader(sponsor.name)

        sponsor.description?.let {
            addBody(it)
        }

        for (item in sponsorInfo.speakers) {
            addSpeaker(item)
        }
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