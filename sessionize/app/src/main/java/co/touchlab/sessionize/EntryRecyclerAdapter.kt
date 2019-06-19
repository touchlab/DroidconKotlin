package co.touchlab.sessionize

import android.app.Activity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.speaker.SpeakerFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_about_info.view.*
import kotlinx.android.synthetic.main.item_setting_switch.view.*

open class EntryRecyclerAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<Detail>()


    protected fun removeAll(){
        data.clear()
    }

    protected fun addBody(description: String, icon:Int = 0) {
        data.add(TextDetail(EntryType.TYPE_BODY, description, icon))
    }

    fun addSwitchRow(description: String, icon:Int, isChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
        data.add(SwitchDetail(EntryType.TYPE_SWITCH, description, icon, isChecked, listener))
    }

    fun addButtonRow(description: String, icon:Int, listener: View.OnClickListener) {
        data.add(ButtonDetail(EntryType.TYPE_BUTTON, description, icon, listener))
    }

    protected fun addHeader(title: String) {
        data.add(HeaderDetail(EntryType.TYPE_HEADER, title))
    }

    protected fun addInfo(description: String) {
        data.add(TextDetail(EntryType.TYPE_INFO, description, 0))
    }

    protected fun addSpeaker(speaker: UserAccount) {
        data.add(SpeakerDetail(EntryType.TYPE_SPEAKER,
                speaker.profilePicture,
                speaker.fullName,
                speaker.tagLine,
                speaker.bio,
                speaker.id))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_event_text, parent, false)
        return TextVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (EntryType.values()[holder.itemViewType]) {
            EntryType.TYPE_HEADER -> {
                val view = (holder as HeaderVH).itemView
                view.findViewById<TextView>(R.id.title).text = (data[position] as HeaderDetail).title
            }

            EntryType.TYPE_INFO -> {
                val view = (holder as InfoVH).itemView
                view.findViewById<TextView>(R.id.info).text = Html.fromHtml((data[position] as TextDetail).text.trim())
            }

            EntryType.TYPE_BODY -> {
                val view = (holder as TextVH).itemView
                val detail = (data[position] as TextDetail)
                view.body.text = Html.fromHtml((data[position] as TextDetail).text.trim())
                view.image?.setImageResource(detail.icon)
            }

            EntryType.TYPE_SPEAKER -> {
                val view = (holder as SpeakerVH).itemView
                val user = data[position] as SpeakerDetail

                if (!user.avatar.isNullOrBlank()) {
                    Picasso.get()
                            .load(user.avatar)
                            .noFade()
                            .placeholder(R.drawable.circle_profile_placeholder)
                            .into(view.findViewById<ImageView>(R.id.profile_image))
                }

                val companyName = if (user.company.isNullOrEmpty()) "" else user.company
                view.findViewById<TextView>(R.id.name).text = activity.getString(R.string.event_speaker_name).format(user.name, companyName)

                view.setOnClickListener {
                    (activity as NavigationHost).navigateTo(SpeakerFragment.newInstance(user.userId), true)
                }
                if(user.bio == null)
                    view.findViewById<TextView>(R.id.bio).text =""
                else
                    view.findViewById<TextView>(R.id.bio).text = Html.fromHtml(user.bio.trim())
            }

            EntryType.TYPE_SWITCH -> {
                val view = (holder as SwitchVH)
                val detail = (data[position] as SwitchDetail)
                view.switch.text = detail.text.trim()
                view.switch.isChecked = detail.isChecked
                view.image.setImageResource(detail.icon)
                view.switch.setOnCheckedChangeListener(detail.listener)
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
    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].getItemType()
    }

    enum class EntryType{
        TYPE_HEADER,
        TYPE_BODY,
        TYPE_SWITCH,
        TYPE_BUTTON,
        TYPE_INFO,
        TYPE_SPEAKER
    }

    open inner class Detail(val type: EntryType) {
        fun getItemType(): Int {
            return type.ordinal
        }
    }

    inner class TextDetail(type: EntryType, val text: String, val icon: Int) : Detail(type)
    inner class ButtonDetail(type: EntryType, val text: String, val icon: Int, val listener: View.OnClickListener) : Detail(type)
    inner class SwitchDetail(type: EntryType, val text: String, val icon: Int, var isChecked: Boolean, var listener: CompoundButton.OnCheckedChangeListener) : Detail(type)
    inner class HeaderDetail(type: EntryType, val title: String) : Detail(type)
    inner class SpeakerDetail(type: EntryType, val avatar: String?, val name: String, val company: String?, val bio: String?, val userId: String) : Detail(type)


    inner class HeaderVH(val item: View) : RecyclerView.ViewHolder(item)
    inner class InfoVH(val item: View) : RecyclerView.ViewHolder(item)
    inner class SpeakerVH(val item: View) : RecyclerView.ViewHolder(item)


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