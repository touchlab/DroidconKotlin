package co.touchlab.sessionize.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.databinding.ItemEventBinding
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
        return ScheduleBlockViewHolder(ItemEventBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
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

    inner class ScheduleBlockViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(scheduleBlockHour: HourBlock) {
            binding.time.text = scheduleBlockHour.hourStringDisplay
            binding.title.text = scheduleBlockHour.timeBlock.title
            binding.speaker.text = scheduleBlockHour.speakerText
            binding.eventDescription.text = scheduleBlockHour.timeBlock.description
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
            binding.rsvpIndicator.visibility = View.VISIBLE
            when (state) {
                RsvpState.None -> {
                    binding.rsvpIndicator.visibility = View.INVISIBLE
                }
                RsvpState.Rsvp -> {
                    binding.rsvpIndicator.setImageResource(R.drawable.rsvp_vector)
                }
                RsvpState.Conflict -> {
                    binding.rsvpIndicator.setImageResource(R.drawable.rsvp_conflict_vector)
                }
                RsvpState.RsvpPast -> {
                    binding.rsvpIndicator.setImageResource(R.drawable.rsvp_past_vector)
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
            binding.card.setOnClickListener { listener() }
        }
    }
}