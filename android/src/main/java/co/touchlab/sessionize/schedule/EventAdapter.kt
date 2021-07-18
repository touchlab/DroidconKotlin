package co.touchlab.sessionize.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.display.HourBlock
import co.touchlab.sessionize.display.RsvpState

/**
 *
 * Created by izzyoji :) on 8/6/15.
 */
class EventAdapter(private val context: Context,
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
        (holder as ScheduleBlockViewHolder).bind(dataSet[position])
    }

    private fun updateData() {
        notifyDataSetChanged()
    }

    fun updateEvents(data: List<HourBlock>) {
        dataSet = data
        updateData()
    }

    inner class ScheduleBlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.title)
        private val time = itemView.findViewById<TextView>(R.id.time)
        private val speaker = itemView.findViewById<TextView>(R.id.speaker)
        private val description = itemView.findViewById<TextView>(R.id.event_description)
        private val rsvp = itemView.findViewById<ImageView>(R.id.rsvpIndicator)
        private val card = itemView.findViewById<CardView>(R.id.card)

        fun bind(scheduleBlockHour: HourBlock) {
            time.text = scheduleBlockHour.hourStringDisplay
            title.text = scheduleBlockHour.timeBlock.title
            speaker.text = scheduleBlockHour.speakerText
            description.text = scheduleBlockHour.timeBlock.description
            setRsvpState(scheduleBlockHour.getRsvpState(allEvents, dataSet))
            setPast(scheduleBlockHour.isPast())
            setTimeGap(scheduleBlockHour.timeGap)

            if (!scheduleBlockHour.timeBlock.isBlock()) {
                setOnClickListener { eventClickListener(scheduleBlockHour) }
            } else {
                setOnClickListener {}
            }
        }

        private fun setRsvpState(state: RsvpState) {
            rsvp.visibility = View.VISIBLE
            when (state) {
                RsvpState.None -> {
                    rsvp.visibility = View.INVISIBLE
                }
                RsvpState.Rsvp -> {
                    rsvp.setImageResource(R.drawable.rsvp_vector)
                }
                RsvpState.Conflict -> {
                    rsvp.setImageResource(R.drawable.rsvp_conflict_vector)
                }
                RsvpState.RsvpPast -> {
                    rsvp.setImageResource(R.drawable.rsvp_past_vector)
                }
            }
        }

        private fun setPast(b: Boolean) {
            val color = if (b) R.color.pastEvent else R.color.white
            val cardView = itemView.findViewById<CardView>(R.id.card)
               cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, color))
        }

        private fun setTimeGap(gap: Boolean) {
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

        private fun setOnClickListener(listener: () -> Unit) {
            card.setOnClickListener { listener() }
        }
    }
}