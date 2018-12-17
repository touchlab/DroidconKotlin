package co.touchlab.sessionize.speaker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.SpeakerInfo
import co.touchlab.sessionize.SpeakerModel
import co.touchlab.sessionize.SpeakerUiData
import com.squareup.picasso.Picasso


class SpeakerFragment : Fragment() {
    companion object {
        val USER_ID = "userId"

        fun newInstance(userId: String): SpeakerFragment {
            return SpeakerFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
        }
    }

    val userId: String by lazy { arguments?.getString(USER_ID) ?: "" }
    lateinit var speakerViewModel:SpeakerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speakerViewModel = ViewModelProviders.of(this, SpeakerViewModelFactory(userId))[SpeakerViewModel::class.java]

    }

    lateinit var mainView : View
    lateinit var speakerInfoAdapter: SpeakerInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_speaker, container, false)
        speakerViewModel.speakerModel.register(object : SpeakerModel.View{
            override fun update(speakerUiData: SpeakerUiData) {
                updateDisplay(speakerUiData)
            }
        })
        val list = mainView.findViewById<RecyclerView>(R.id.speakerInfoList)
        list.layoutManager = LinearLayoutManager(activity)
        speakerInfoAdapter = SpeakerInfoAdapter()
        list.adapter = speakerInfoAdapter

        return mainView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speakerViewModel.speakerModel.shutDown()
    }

    private fun updateDisplay(speakerUiData: SpeakerUiData) {
        mainView.findViewById<TextView>(R.id.name).text = speakerUiData.fullName
        val companyView = mainView.findViewById<TextView>(R.id.company)
        if(speakerUiData.company.isNullOrBlank()) {
            companyView.visibility = View.GONE
        }
        else{
            companyView.visibility = View.VISIBLE
            companyView.text = speakerUiData.company
        }
        if(speakerUiData.profilePicture != null){
            Picasso.get().load(speakerUiData.profilePicture).into(mainView.findViewById<ImageView>(R.id.profile_image))
        }
        speakerInfoAdapter.updateDate(speakerUiData.infoRows)
        speakerUiData.sessions.forEach {
            println(it.title)
        }
    }

    inner class SpeakerInfoAdapter : RecyclerView.Adapter<SpeakerInfoViewHolder>() {

        var infoList:List<SpeakerInfo> = emptyList()

        fun updateDate(data:List<SpeakerInfo>){
            infoList = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerInfoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_speaker_info, parent, false)

            return SpeakerInfoViewHolder(view)
        }

        override fun getItemCount(): Int = infoList.size

        override fun onBindViewHolder(holder: SpeakerInfoViewHolder, position: Int) {
            val speakerInfo = infoList.get(position)
            holder.infoTextView.text = speakerInfo.info
            holder.infoIconView.setImageResource(finDrawableId(requireContext(), speakerInfo.type.icon))
        }
    }

    class SpeakerInfoViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val infoTextView = itemView.findViewById<TextView>(R.id.infoTextView)
        val infoIconView = itemView.findViewById<ImageView>(R.id.infoIconView)
    }
}

fun finDrawableId(ctx: Context, str: String): Int = ctx.getResources().getIdentifier(str, "drawable", ctx.getPackageName())

class SpeakerViewModel(userId:String):ViewModel(){
    val speakerModel = SpeakerModel(userId)
}

class SpeakerViewModelFactory(private val userId:String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SpeakerViewModel(userId) as T
    }
}
