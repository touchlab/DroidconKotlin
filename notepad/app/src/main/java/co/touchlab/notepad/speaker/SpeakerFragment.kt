package co.touchlab.notepad.speaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import co.touchlab.notepad.R
import co.touchlab.notepad.SpeakerModel
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_speaker, container, false)
        speakerViewModel.speakerModel.uiLiveData().observe(viewLifecycleOwner, Observer {
            updateDisplay(it)
        })
        return mainView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speakerViewModel.speakerModel.shutDown()
    }

    private fun updateDisplay(speakerUiData: SpeakerModel.SpeakerUiData) {
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
    }
}

class SpeakerViewModel(userId:String):ViewModel(){
    val speakerModel = SpeakerModel(userId)
}

class SpeakerViewModelFactory(private val userId:String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SpeakerViewModel(userId) as T
    }
}
