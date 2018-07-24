package co.touchlab.notepad.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.notepad.R
import co.touchlab.notepad.db.isBlock
import co.touchlab.notepad.display.HourBlock
import co.touchlab.notepad.display.isPast
import co.touchlab.notepad.utils.setViewVisibility
import java.lang.UnsupportedOperationException

/**
 *
 * Created by izzyoji :) on 8/6/15.
 */

class EventAdapter(private val context: Context,
                   private val allEvents: Boolean,
                   private val eventClickListener: ((event: HourBlock)->Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: List<HourBlock> = emptyList()

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        println("fuckers")
        return when (viewType) {
            VIEW_TYPE_EVENT -> {
                val v = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
                ScheduleBlockViewHolder(v)
            }
            VIEW_TYPE_PAST_EVENT, VIEW_TYPE_BLOCK -> {
                val v = LayoutInflater.from(context).inflate(R.layout.item_block, parent, false)
                ScheduleBlockViewHolder(v)
            }
            VIEW_TYPE_NEW_ROW -> {
                val v = LayoutInflater.from(context).inflate(R.layout.item_new_row, parent, false)
                NewRowViewHolder(v)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ScheduleBlockViewHolder) {
            val scheduleBlockHour = dataSet[position]

            scheduleBlockHour.let {
                EventUtils.styleEventRow(scheduleBlockHour, dataSet, holder, allEvents)

                if (!scheduleBlockHour.timeBlock.isBlock()) {
                    holder.setOnClickListener { eventClickListener(scheduleBlockHour) }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

//        if (dataSet[position] == null) {
//            return VIEW_TYPE_NEW_ROW
//        }

        val item = dataSet[position]

        return if(item.timeBlock.isBlock())
        {
            VIEW_TYPE_BLOCK
        }
        else
        {
            if(item.isPast())
                VIEW_TYPE_PAST_EVENT
            else
                VIEW_TYPE_EVENT
        }
    }

    private fun updateData() {
//        filteredData.clear()
        /*for (item in dataSet) {
            val position = filteredData.size
            if (item.hourStringDisplay.isNotBlank() && position.isOdd()) {
                // Insert an empty block to indicate a new row
                filteredData.add(null)
            }
            else
            {
                Log.e("EventAdapter", "What not odd?!")
            }
            filteredData.add(item)
        }*/

//        filteredData = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    fun updateEvents(data: List<HourBlock>) {
        dataSet = data
        updateData()
    }

    inner abstract class ScheduleCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ScheduleBlockViewHolder(itemView: View) : ScheduleCardViewHolder(itemView), EventUtils.EventRow {

        override fun setTitleText(s: String) { itemView.findViewById<TextView>(R.id.title).text = s }

        override fun setTimeText(s: String) { itemView.findViewById<TextView>(R.id.time).text = s }

        override fun setSpeakerText(s: String) {
            itemView.findViewById<TextView>(R.id.speaker).text = s
        }

        override fun setDescription(s: String) {
            // Field only exists for tablet
            itemView.findViewById<TextView>(R.id.event_description)?.text = s
        }

        override fun setRsvpVisible(rsvp: Boolean, past: Boolean) {
            val rsvpColor = if (past) ContextCompat.getColor(itemView.context, R.color.card_text_subtitle)
            else ContextCompat.getColor(itemView.context, R.color.accent)
            itemView.findViewById<View>(R.id.rsvp).setBackgroundColor(rsvpColor)
            itemView.findViewById<View>(R.id.rsvp).setViewVisibility(rsvp)
        }

        override fun setRsvpConflict(hasConflict: Boolean) {
            itemView.findViewById<TextView>(R.id.conflict_text).setViewVisibility(hasConflict)
            if (hasConflict)
                itemView.findViewById<View>(R.id.rsvp).setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
        }

        override fun setLiveNowVisible(liveNow: Boolean) {
            itemView.findViewById<ImageView>(R.id.live).setViewVisibility(liveNow)
        }

        override fun setTimeGap(gap: Boolean) {
            if (!isTablet(itemView)) {
                val topPadding = if (gap) R.dimen.padding_small else R.dimen.padding_xmicro
                itemView.setPadding(itemView.paddingLeft,
                        itemView.context.resources.getDimensionPixelOffset(topPadding),
                        itemView.paddingRight,
                        itemView.paddingBottom)
            }
        }

        fun setOnClickListener(listener: () -> Unit) {
            itemView.findViewById<CardView>(R.id.card).setOnClickListener { listener() }
        }

        fun isTablet(itemView: View): Boolean {
            //TODO: This?
            return false
        }
    }

    inner class NewRowViewHolder(itemView: View) : ScheduleCardViewHolder(itemView)

    companion object {
        private val VIEW_TYPE_EVENT = 0
        private val VIEW_TYPE_BLOCK = 1
        private val VIEW_TYPE_PAST_EVENT = 2
        private val VIEW_TYPE_NEW_ROW = 4
        private val HEADER_ITEMS_COUNT = 1
    }
}


data class UpdateAllowNotificationEvent(val allow: Boolean)