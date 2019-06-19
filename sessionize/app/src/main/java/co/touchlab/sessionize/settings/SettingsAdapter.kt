package co.touchlab.sessionize.settings

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.EntryRecyclerAdapter


class SettingsAdapter(private val activity: Activity) : EntryRecyclerAdapter(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EntryType.values()[viewType]) {
            // Settings
            EntryType.TYPE_BODY -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_text, parent, false)
                TextVH(view)
            }
            EntryType.TYPE_SWITCH -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_switch, parent, false)
                SwitchVH(view)
            }
            EntryType.TYPE_BUTTON -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_text, parent, false)
                ButtonVH(view)
            }
            else -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_text, parent, false)
                TextVH(view)
            }
        }
    }
}