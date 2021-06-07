package co.touchlab.sessionize.settings

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.databinding.ItemSettingSwitchBinding
import co.touchlab.sessionize.databinding.ItemSettingTextBinding


class SettingsAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<Detail>()


    fun addTextRow(description: String, icon: Int) {
        data.add(TextDetail(EntryType.TYPE_BODY, description, icon))
    }

    fun addSwitchRow(description: String, icon: Int, isChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
        data.add(SwitchDetail(EntryType.TYPE_SWITCH, description, icon, isChecked, listener))
    }

    fun addButtonRow(description: String, icon: Int, listener: View.OnClickListener) {
        data.add(ButtonDetail(EntryType.TYPE_BUTTON, description, icon, listener))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EntryType.values()[viewType]) {
            EntryType.TYPE_BODY -> {
                TextVH(ItemSettingTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            EntryType.TYPE_SWITCH -> {
                SwitchVH(ItemSettingSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            EntryType.TYPE_BUTTON -> {
                ButtonVH(ItemSettingTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
                view.binding.run {
                    settingSwitch.text = detail.text.trim()
                    settingSwitch.isChecked = detail.isChecked
                    image.setImageResource(detail.icon)
                    settingSwitch.setOnCheckedChangeListener(detail.listener)
                }
            }

            EntryType.TYPE_BODY -> {
                val view = (holder as TextVH)
                val detail = (data[position] as TextDetail)
                view.binding.run {
                    body.text = detail.text.trim()
                    image.setImageResource(detail.icon)
                }
            }
            EntryType.TYPE_BUTTON -> {
                val view = (holder as ButtonVH)
                val detail = (data[position] as ButtonDetail)
                view.binding.run {
                    body.text = detail.text.trim()
                    image.setImageResource(detail.icon)
                    view.itemView.setOnClickListener(detail.listener)
                }
            }
        }
    }

    enum class EntryType {
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
    inner class SwitchDetail(type: EntryType, val text: String, val icon: Int, var isChecked: Boolean, var listener: CompoundButton.OnCheckedChangeListener) : Detail(type)

    inner class SwitchVH(val binding: ItemSettingSwitchBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ButtonVH(val binding: ItemSettingTextBinding) : RecyclerView.ViewHolder(binding.root)
    inner class TextVH(val binding: ItemSettingTextBinding) : RecyclerView.ViewHolder(binding.root)

}