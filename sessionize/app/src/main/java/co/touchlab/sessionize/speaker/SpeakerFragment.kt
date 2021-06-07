package co.touchlab.sessionize.speaker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.SpeakerInfo
import co.touchlab.sessionize.SpeakerModel
import co.touchlab.sessionize.SpeakerUiData
import co.touchlab.sessionize.databinding.FragmentSpeakerBinding
import co.touchlab.sessionize.databinding.ItemSpeakerInfoBinding
import co.touchlab.sessionize.util.viewBindingLifecycle
import com.squareup.picasso.Picasso


class SpeakerFragment : Fragment() {

    private var binding by viewBindingLifecycle<FragmentSpeakerBinding>()

    val userId: String by lazy {
        arguments?.let {
            val speakerArgs = SpeakerFragmentArgs.fromBundle(it)
            speakerArgs.userid
        } ?: ""
    }

    private val speakerViewModel: SpeakerViewModel by viewModels(factoryProducer = {
        SpeakerViewModelFactory(userId)
    })

    lateinit var mainView: View
    lateinit var speakerInfoAdapter: SpeakerInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSpeakerBinding.inflate(inflater, container, false)
        speakerViewModel.speakerModel.register(object : SpeakerModel.SpeakerView {
            override suspend fun update(data: UserAccount) {
                updateDisplay(speakerViewModel.speakerModel.speakerUiData(data))
            }
        })
        binding.speakerInfoList.apply {
            layoutManager = LinearLayoutManager(activity)
            speakerInfoAdapter = SpeakerInfoAdapter()
            adapter = speakerInfoAdapter
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speakerViewModel.speakerModel.shutDown()
    }

    private fun updateDisplay(speakerUiData: SpeakerUiData) {
        binding.name.text = speakerUiData.fullName

        if (speakerUiData.company.isNullOrBlank()) {
            binding.company.visibility = View.GONE
        } else {
            binding.company.visibility = View.VISIBLE
            binding.company.text = speakerUiData.company
        }
        if (speakerUiData.profilePicture != null) {
            Picasso.get().load(speakerUiData.profilePicture).into(binding.profileImage)
        }
        speakerInfoAdapter.updateDate(speakerUiData.infoRows)
        speakerUiData.sessions.forEach {
            println(it.title)
        }
    }

    inner class SpeakerInfoAdapter : RecyclerView.Adapter<SpeakerInfoViewHolder>() {

        var infoList: List<SpeakerInfo> = emptyList()

        fun updateDate(data: List<SpeakerInfo>) {
            infoList = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerInfoViewHolder {
            val binding = ItemSpeakerInfoBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            return SpeakerInfoViewHolder(binding)
        }

        override fun getItemCount(): Int = infoList.size

        override fun onBindViewHolder(holder: SpeakerInfoViewHolder, position: Int) {
            val speakerInfo = infoList[position]
            holder.binding.infoTextView.text = speakerInfo.info
            holder.binding.infoIconView.setImageResource(finDrawableId(requireContext(), speakerInfo.type.icon))
        }
    }

    class SpeakerInfoViewHolder(val binding: ItemSpeakerInfoBinding) : RecyclerView.ViewHolder(binding.root)
}

fun finDrawableId(ctx: Context, str: String): Int = ctx.resources.getIdentifier(str, "drawable", ctx.packageName)

class SpeakerViewModel(userId: String) : ViewModel() {
    val speakerModel = SpeakerModel(userId)
}

class SpeakerViewModelFactory(private val userId: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SpeakerViewModel(userId) as T
    }
}
