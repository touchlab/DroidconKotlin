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
import co.touchlab.notepad.EventRow
import co.touchlab.notepad.R
import co.touchlab.notepad.ScheduleModel
import co.touchlab.notepad.db.isBlock
import co.touchlab.notepad.display.HourBlock
import co.touchlab.notepad.display.RowType
import co.touchlab.notepad.utils.setViewVisibility

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
        val rowType = RowType.values()[viewType]
        val layoutId = when (rowType) {
            RowType.FutureEvent -> {
                R.layout.item_event
            }
            RowType.PastEvent, RowType.Block -> {
                R.layout.item_block
            }
        }

        val v = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return ScheduleBlockViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val scheduleHolder = holder as ScheduleBlockViewHolder
        val scheduleBlockHour = dataSet[position]

        scheduleModel.weaveSessionDetailsUi(scheduleBlockHour, dataSet, scheduleHolder, allEvents)
        if (!scheduleBlockHour.timeBlock.isBlock()) {
            scheduleHolder.setOnClickListener { eventClickListener(scheduleBlockHour) }
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
            val topPadding = if (gap) R.dimen.padding_small else R.dimen.padding_xmicro
            itemView.setPadding(itemView.paddingLeft,
                    itemView.context.resources.getDimensionPixelOffset(topPadding),
                    itemView.paddingRight,
                    itemView.paddingBottom)
        }

        fun setOnClickListener(listener: () -> Unit) {
            itemView.findViewById<CardView>(R.id.card).setOnClickListener { listener() }
        }
    }
}