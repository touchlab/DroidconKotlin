package co.touchlab.sessionize.sponsors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import com.nex3z.flowlayout.FlowLayout
import com.squareup.picasso.Picasso
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import co.touchlab.sessionize.FragmentAnimation
import co.touchlab.sessionize.NavigationHost
import co.touchlab.sessionize.db.SponsorGroupDbItem


class SponsorsFragment : Fragment() {

    lateinit var adapter: SponsorGroupAdapter
    lateinit var sponsorViewModel:SponsorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sponsorViewModel = ViewModelProviders.of(this, ViewModelProvider.NewInstanceFactory())[SponsorViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sponsor, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)

        sponsorViewModel.registerForChanges {
            adapter.sponsorGroupDbItems = it
            adapter.notifyDataSetChanged()
        }

        adapter = SponsorGroupAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sponsorViewModel.unregister()
    }

    fun navigateToSponsor(sponsorId: String, groupName: String) {
        (activity as NavigationHost).navigateTo(
                SponsorSessionFragment.newInstance(sponsorId, groupName),
                true,
                FragmentAnimation(R.anim.slide_from_right,
                        R.anim.slide_to_left,
                        R.anim.slide_from_left,
                        R.anim.slide_to_right)
        )
    }

    inner class SponsorGroupAdapter : RecyclerView.Adapter<SponsorGroupViewHolder>(){
        var sponsorGroupDbItems:List<SponsorGroupDbItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorGroupViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sponsor_group, parent, false)

            return SponsorGroupViewHolder(view)
        }

        override fun getItemCount(): Int = sponsorGroupDbItems.size

        override fun onBindViewHolder(holder: SponsorGroupViewHolder, position: Int) {
            val sponsorGroup = sponsorGroupDbItems.get(position)
            holder.groupName.text = sponsorGroup.groupName
            holder.flowGroup.removeAllViews()
            val layoutInflater = LayoutInflater.from(activity)
            val itemLayout = if(sponsorGroup.groupName.contains("Gold")){R.layout.item_sponsor_pic}else{R.layout.item_sponsor_pic_small}
            for (sponsor in sponsorGroup.sponsors) {
                val iv = layoutInflater.inflate(itemLayout, holder.flowGroup, false) as ImageView
                Picasso.get().load(sponsor.icon).into(iv)
                holder.flowGroup.addView(iv)
                iv.setOnClickListener {
                    if (sponsor.sponsorId.isNullOrBlank()) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(sponsor.url)
                        startActivity(i)
                    } else {
                        sponsor.sponsorId?.let {
                            navigateToSponsor(it, sponsor.groupName)
                        }
                    }
                }
            }
        }
    }

    class SponsorGroupViewHolder(view:View):RecyclerView.ViewHolder(view){
        val groupName = view.findViewById<TextView>(R.id.groupName)
        val flowGroup = view.findViewById<FlowLayout>(R.id.flowGroup)
    }
}
