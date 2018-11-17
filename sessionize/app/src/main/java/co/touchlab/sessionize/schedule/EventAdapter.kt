package co.touchlab.sessionize.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.EventRow
import co.touchlab.sessionize.R
import co.touchlab.sessionize.RsvpState
import co.touchlab.sessionize.ScheduleModel
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.display.HourBlock

/**
 *
 * Created by izzyoji :) on 8/6/15.
 */
class EventAdapter(private val context: Context,
                   private val scheduleModel: ScheduleModel,
                   private val allEvents: Boolean,
                   private val eventClickListener: ((event: HourBlock) -> Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: List<HourBlock> = emptyList()

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return ScheduleBlockViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val scheduleHolder = holder as ScheduleBlockViewHolder
        val scheduleBlockHour = dataSet[position]

        scheduleModel.weaveSessionDetailsUi(scheduleBlockHour, dataSet, scheduleHolder, allEvents)
        if (!scheduleBlockHour.timeBlock.isBlock()) {
            scheduleHolder.setOnClickListener { eventClickListener(scheduleBlockHour) }
        }else{
            scheduleHolder.setOnClickListener {}
        }
    }

    private fun updateData() {
        notifyDataSetChanged()
    }

    fun updateEvents(data: List<HourBlock>) {
        dataSet = data
        updateData()
    }

    inner class ScheduleBlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), EventRow {

        override fun setTitleText(s: String) {
            itemView.findViewById<TextView>(R.id.title).text = s
        }

        override fun setTimeText(s: String) {
            itemView.findViewById<TextView>(R.id.time).text = s
        }

        override fun setSpeakerText(s: String) {
            itemView.findViewById<TextView>(R.id.speaker).text = s
        }

        override fun setDescription(s: String) {
            // Field only exists for tablet
            itemView.findViewById<TextView>(R.id.event_description)?.text = s
        }

        override fun setLiveNowVisible(liveNow: Boolean) {
            itemView.findViewById<ImageView>(R.id.live)
                    .visibility = if (liveNow) View.VISIBLE else View.INVISIBLE
        }

        override fun setRsvpState(state: RsvpState) {
            val imageView = itemView.findViewById<ImageView>(R.id.rsvpIndicator)
            imageView.visibility = View.VISIBLE
            when(state){
                RsvpState.None -> {
                    imageView.visibility = View.INVISIBLE
                }
                RsvpState.Rsvp -> {
                    imageView.setImageResource(R.drawable.rsvp_vector)
                }
                RsvpState.Conflict -> {
                    imageView.setImageResource(R.drawable.rsvp_conflict_vector)
                }
                RsvpState.RsvpPast -> {
                    imageView.setImageResource(R.drawable.rsvp_past_vector)
                }
            }
        }

        override fun setTimeGap(gap: Boolean) {
            val topPadding = if (gap) R.dimen.padding_small else R.dimen.padding_xmicro
            val offset = itemView.context.resources.getDimensionPixelOffset(topPadding)
            itemView.setPadding(itemView.paddingLeft,
                    offset,
                    itemView.paddingRight,
                    itemView.paddingBottom)
            val rsvpIndicator = itemView.findViewById<ImageView>(R.id.rsvpIndicator)
            rsvpIndicator.setPadding(rsvpIndicator.paddingLeft,
                    offset,
                    rsvpIndicator.paddingRight,
                    rsvpIndicator.paddingBottom)
        }

        fun setOnClickListener(listener: () -> Unit) {
            itemView.findViewById<CardView>(R.id.card).setOnClickListener { listener() }
        }
    }
}