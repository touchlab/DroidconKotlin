package co.touchlab.sessionize.settings

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R


class SettingsAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<Detail>()

    fun addTextRow(description: String, icon:Int) {
        data.add(TextDetail(EntryType.TYPE_BODY, description, icon))
    }

    fun addSwitchRow(description: String, icon:Int, isChecked: Boolean) {
        data.add(SwitchDetail(EntryType.TYPE_SWITCH, description, icon, isChecked))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EntryType.values()[viewType]) {
            EntryType.TYPE_BODY -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_text, parent, false)
                TextVH(view)
            }
            EntryType.TYPE_SWITCH -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_switch, parent, false)
                SwitchVH(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].getItemType()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (EntryType.values()[holder.itemViewType]) {
            EntryType.TYPE_SWITCH -> {
                val view = (holder as SwitchVH).itemView
                view.findViewById<Switch>(R.id.settingSwitch).text = (data[position] as SwitchDetail).text.trim()
                view.findViewById<Switch>(R.id.settingSwitch).isChecked = (data[position] as SwitchDetail).isChecked
                view.findViewById<ImageView>(R.id.image).setImageDrawable(activity.resources.getDrawable( (data[position] as SwitchDetail).icon,null))

            }

            EntryType.TYPE_BODY -> {
                val view = (holder as TextVH).itemView
                view.findViewById<TextView>(R.id.body).text = (data[position] as TextDetail).text.trim()
                view.findViewById<ImageView>(R.id.image).setImageDrawable(activity.resources.getDrawable( (data[position] as TextDetail).icon,null))

                view.setOnClickListener {
                    print("test")
                }
            }
        }
    }

    enum class EntryType{
        TYPE_BODY,
        TYPE_SWITCH
    }

    open inner class Detail(val type: EntryType) {
        fun getItemType(): Int {
            return type.ordinal
        }
    }

    inner class TextDetail(type: EntryType, val text: String, val icon: Int) : Detail(type)
    inner class SwitchDetail(type: EntryType, val text: String, val icon: Int, var isChecked: Boolean) : Detail(type)

    inner class SwitchVH(val item: View) : RecyclerView.ViewHolder(item)

    inner class TextVH(val item: View) : RecyclerView.ViewHolder(item)

}