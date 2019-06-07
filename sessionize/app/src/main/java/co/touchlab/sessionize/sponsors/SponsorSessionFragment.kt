package co.touchlab.sessionize.sponsors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.SponsorSessionInfo
import co.touchlab.sessionize.SponsorSessionModel

class SponsorSessionFragment : Fragment() {
    companion object {
        val SPONSOR_SESSION_ID = "sponsorId"
        val SPONSOR_GROUP_NAME = "groupName"


        fun newInstance(sponsorId: String, groupName: String): SponsorSessionFragment {
            return SponsorSessionFragment().apply {
                arguments = Bundle().apply {
                    putString(SPONSOR_SESSION_ID, sponsorId)
                    putString(SPONSOR_GROUP_NAME, groupName)
                }
            }
        }
    }

    val sponsorId: String by lazy { arguments?.getString(SponsorSessionFragment.SPONSOR_SESSION_ID) ?: "" }
    val groupName: String by lazy { arguments?.getString(SponsorSessionFragment.SPONSOR_GROUP_NAME) ?: "" }

    lateinit var sponsorSessionViewModel: SponsorSessionViewModel
    lateinit var recycler: RecyclerView
    lateinit var sponsorSessionTitle: TextView
    lateinit var sponsorSessionRoomTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sponsorSessionViewModel = ViewModelProviders.of(
                this,
                SponsorSessionViewModelFactory(sponsorId, groupName)
        )[SponsorSessionViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
                R.layout.fragment_sponsor_session,
                container,
                false
        )

        sponsorSessionTitle= view.findViewById(R.id.sponsorSessionTitle)
        sponsorSessionRoomTime = view.findViewById(R.id.sponsorSessionRoomTime)
        recycler = view.findViewById(R.id.recycler)

        sponsorSessionViewModel
                .sponsorSessionModel
                .register(object : SponsorSessionModel.SponsorSessionView {
            override suspend fun update(data: SponsorSessionInfo) {
                dataRefresh(data)
            }
        })

        return view
    }

    override fun onDestroyView(){
        super.onDestroyView()
        sponsorSessionViewModel.sponsorSessionModel.shutDown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(getActivity())
    }

    private fun dataRefresh(sponsorInfo: SponsorSessionInfo) {
        updateContent(sponsorInfo)
    }

    private fun updateContent(sponsor: SponsorSessionInfo) {
        val adapter = SponsorSessionAdapter(activity!!)

        sponsorSessionTitle.text = sponsor.sponsor.name
        adapter.addHeader(sponsor.sponsor.name)

        sponsor.sponsor.description?.let {
            adapter.addBody(it)
        }

        for (item in sponsor.speakers) {
            adapter.addSpeaker(item)
        }

        recycler.adapter = adapter
    }
}
