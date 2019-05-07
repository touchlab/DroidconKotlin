package co.touchlab.sessionize.settings

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import kotlinx.android.synthetic.main.item_about_info.view.*
import kotlinx.android.synthetic.main.item_setting_switch.view.*


class SettingsAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<Detail>()


    fun addTextRow(description: String, icon:Int) {
        data.add(TextDetail(EntryType.TYPE_BODY, description, icon))
    }

    fun addSwitchRow(description: String, icon:Int, isChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
        data.add(SwitchDetail(EntryType.TYPE_SWITCH, description, icon, isChecked, listener))
    }

    fun addButtonRow(description: String, icon:Int, listener: View.OnClickListener) {
        data.add(ButtonDetail(EntryType.TYPE_BUTTON, description, icon, listener))
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
            EntryType.TYPE_BUTTON -> {
                val view = LayoutInflater.from(activity).inflate(R.layout.item_setting_text, parent, false)
                ButtonVH(view)
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
                val view = (holder as SwitchVH)
                val detail = (data[position] as SwitchDetail)
                view.switch.text = detail.text.trim()
                view.switch.isChecked = detail.isChecked
                view.image.setImageResource(detail.icon)
                view.switch.setOnCheckedChangeListener(detail.listener)
            }

            EntryType.TYPE_BODY -> {
                val view = (holder as TextVH)
                val detail = (data[position] as TextDetail)
                view.body.text = detail.text.trim()
                view.image.setImageResource(detail.icon)
            }
            EntryType.TYPE_BUTTON -> {
                val view = (holder as ButtonVH)
                val detail = (data[position] as ButtonDetail)
                view.body.text = detail.text.trim()
                view.image.setImageResource(detail.icon)
                view.itemView.setOnClickListener(detail.listener)
            }
        }
    }

    enum class EntryType{
        TYPE_BODY,
        TYPE_SWITCH,
        TYPE_BUTTON
    }

    open inner class Detail(val type: EntryType) {
        fun getItemType(): Int {
            return type.ordinal
        }
    }

    inner class TextDetail(type: EntryType, val text: String, val icon: Int) : Detail(type)
    inner class ButtonDetail(type: EntryType, val text: String, val icon: Int, val listener: View.OnClickListener) : Detail(type)
    inner class SwitchDetail(type: EntryType, val text: String, val icon: Int, var isChecked: Boolean, var listener:CompoundButton.OnCheckedChangeListener) : Detail(type)

    inner class SwitchVH(val item: View) : RecyclerView.ViewHolder(item){
        val switch = item.settingSwitch!!
        val image = item.image!!
    }
    inner class ButtonVH(val item: View) : RecyclerView.ViewHolder(item){
        val body = item.body!!
        val image = item.image!!
    }
    inner class TextVH(val item: View) : RecyclerView.ViewHolder(item){
        val body = item.body!!
        val image = item.image!!
    }

}