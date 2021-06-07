package co.touchlab.sessionize.event

import android.app.Activity
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.R
import co.touchlab.sessionize.databinding.ItemEventHeaderBinding
import co.touchlab.sessionize.databinding.ItemEventInfoBinding
import co.touchlab.sessionize.databinding.ItemEventTextBinding
import co.touchlab.sessionize.databinding.ItemSpeakerSummaryBinding
import com.squareup.picasso.Picasso
import java.util.*

class EventDetailAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<Detail>()

    fun addHeader(title: String) {
        data.add(HeaderDetail(EntryType.TYPE_HEADER, title))
    }

    fun addBody(description: String) {
        data.add(TextDetail(EntryType.TYPE_BODY, description, 0))
    }

    fun addInfo(description: String) {
        data.add(TextDetail(EntryType.TYPE_INFO, description, 0))
    }

    fun addSpeaker(speaker: UserAccount) {
        data.add(SpeakerDetail(EntryType.TYPE_SPEAKER,
                speaker.profilePicture,
                speaker.fullName,
                speaker.tagLine,
                speaker.bio,
                speaker.id))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (EntryType.values()[viewType]) {
            EntryType.TYPE_HEADER -> {
                HeaderVH(ItemEventHeaderBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false))
            }
            EntryType.TYPE_BODY -> {
                TextVH(ItemEventTextBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false))
            }
            EntryType.TYPE_INFO -> {
                InfoVH(ItemEventInfoBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false))
            }
            EntryType.TYPE_SPEAKER -> {
                SpeakerVH(ItemSpeakerSummaryBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false))
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
            EntryType.TYPE_HEADER -> {
                val binding = (holder as HeaderVH).binding
                binding.title.text = (data[position] as HeaderDetail).title
            }

            EntryType.TYPE_INFO -> {
                val binding = (holder as InfoVH).binding
                binding.info.text = Html.fromHtml((data[position] as TextDetail).text.trim())
            }

            EntryType.TYPE_BODY -> {
                val binding = (holder as TextVH).binding
                binding.body.text = Html.fromHtml((data[position] as TextDetail).text.trim())
            }

            EntryType.TYPE_SPEAKER -> {
                val binding = (holder as SpeakerVH).binding
                val user = data[position] as SpeakerDetail

                if (!user.avatar.isNullOrBlank()) {
                    Picasso.get()
                            .load(user.avatar)
                            .noFade()
                            .placeholder(R.drawable.circle_profile_placeholder)
                            .into(binding.profileImage)
                }

                val companyName = if (user.company.isNullOrEmpty()) "" else user.company
                binding.name.text = activity.getString(R.string.event_speaker_name).format(user.name, companyName)

                binding.root.setOnClickListener {
                    val direction = EventFragmentDirections.actionEventFragmentToSpeakerFragment(user.userId)
                    binding.root.findNavController().navigate(direction)
                }
                if(user.bio == null)
                    binding.bio.text =""
                else
                    binding.bio.text = Html.fromHtml(user.bio.trim())
            }
        }
    }

    enum class EntryType{
        TYPE_HEADER,
        TYPE_BODY,
        TYPE_INFO,
        TYPE_SPEAKER
    }

    open inner class Detail(val type: EntryType) {
        fun getItemType(): Int {
            return type.ordinal
        }
    }

    inner class HeaderDetail(type: EntryType, val title: String) : Detail(type)

    inner class TextDetail(type: EntryType, val text: String, val icon: Int) : Detail(type)

    inner class SpeakerDetail(type: EntryType, val avatar: String?, val name: String, val company: String?, val bio: String?, val userId: String) : Detail(type)

    inner class HeaderVH(val binding: ItemEventHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    inner class InfoVH(val binding: ItemEventInfoBinding) : RecyclerView.ViewHolder(binding.root)

    inner class TextVH(val binding: ItemEventTextBinding) : RecyclerView.ViewHolder(binding.root)

    inner class SpeakerVH(val binding: ItemSpeakerSummaryBinding) : RecyclerView.ViewHolder(binding.root)
}