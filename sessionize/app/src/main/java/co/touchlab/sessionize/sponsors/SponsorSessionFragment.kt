package co.touchlab.sessionize.sponsors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import co.touchlab.sessionize.SponsorSessionInfo
import co.touchlab.sessionize.databinding.FragmentSponsorSessionBinding
import co.touchlab.sessionize.util.viewBindingLifecycle
import com.squareup.picasso.Picasso

class SponsorSessionFragment : Fragment() {

    private var binding: FragmentSponsorSessionBinding by viewBindingLifecycle()

    val sponsorId: String by lazy {
        arguments?.let {
            val scheduleArgs = SponsorSessionFragmentArgs.fromBundle(it)
            scheduleArgs.sponsorsessionid
        } ?: ""
    }

    val groupName: String by lazy {
        arguments?.let {
            val scheduleArgs = SponsorSessionFragmentArgs.fromBundle(it)
            scheduleArgs.sponsorgroupname
        } ?: ""
    }

    private val sponsorSessionViewModel: SponsorSessionViewModel by viewModels(factoryProducer = {
        SponsorSessionViewModelFactory()
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSponsorSessionBinding.inflate(inflater, container, false)

        val adapter = SponsorSessionAdapter(requireActivity())
        binding.recycler.adapter = adapter

        sponsorSessionViewModel.sponsorSessionModel.loadSponsorDetail({
            dataRefresh(it)
        }){
            Log.e("SponsorSessionFragment", it.message, it)
            activity?.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun dataRefresh(sponsorInfo: SponsorSessionInfo) {
        updateContent(sponsorInfo)
    }

    private fun updateContent(sponsorInfo: SponsorSessionInfo) {
        val adapter = SponsorSessionAdapter(requireActivity())
        val sponsor = sponsorInfo.sponsor
        val sessionDetail = sponsorInfo.sessionDetail

        binding.sponsorSessionTitle.text = sponsor.name
        binding.sponsorGroupName.text = sponsor.groupName
        adapter.addHeader(sponsor.name)

        sponsor.icon.let {
            Picasso.get().load(it).into(binding.sponsorImage)
        }

        adapter.addBody(sessionDetail)

        for (item in sponsorInfo.speakers) {
            adapter.addSpeaker(item)
        }

        binding.recycler.adapter = adapter
    }
}
