package co.touchlab.sessionize.sponsors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.SponsorSessionModel
import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.sponsorClicked
import com.nex3z.flowlayout.FlowLayout
import com.squareup.picasso.Picasso


class SponsorsFragment : Fragment() {

    lateinit var adapter: SponsorGroupAdapter
    lateinit var sponsorViewModel:SponsorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sponsorViewModel = SponsorViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sponsor, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)


        sponsorViewModel.load({
            adapter.sponsorGroupItems = it
            adapter.notifyDataSetChanged()
        }) {
            activity?.let {
                Toast.makeText(
                        it,
                        "Network error. Try again Later.",
                        Toast.LENGTH_LONG
                ).show()
            }

        }

        adapter = SponsorGroupAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class SponsorGroupAdapter : RecyclerView.Adapter<SponsorGroupViewHolder>(){
        var sponsorGroupItems:List<SponsorGroup> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorGroupViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sponsor_group, parent, false)

            return SponsorGroupViewHolder(view)
        }

        override fun getItemCount(): Int = sponsorGroupItems.size

        override fun onBindViewHolder(holder: SponsorGroupViewHolder, position: Int) {
            val sponsorGroup = sponsorGroupItems.get(position)
            holder.groupName.text = sponsorGroup.groupName
            holder.flowGroup.removeAllViews()
            val layoutInflater = LayoutInflater.from(activity)

            val itemLayout = if (
                    sponsorGroup.groupName.contains("Gold") ||
                    sponsorGroup.groupName.contains("Plat") ||
                    sponsorGroup.groupName.contains("Iridium")
            ) {
                R.layout.item_sponsor_pic
            } else {
                R.layout.item_sponsor_pic_small
            }

            for (sponsor in sponsorGroup.sponsors) {
                val iv = layoutInflater.inflate(itemLayout, holder.flowGroup, false) as ImageView
                Picasso.get().load(sponsor.icon).into(iv)
                holder.flowGroup.addView(iv)
                iv.setOnClickListener {

                    sponsorClicked(sponsor)

                    if (sponsor.sponsorId.isNullOrBlank()) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(sponsor.url)
                        startActivity(i)
                    } else {
                        sponsor.sponsorId?.let {
                            SponsorSessionModel.sponsor = sponsor
                            val direction = SponsorsFragmentDirections.actionSponsorsFragmentToSponsorSessionFragment(it, sponsor.groupName)
                            view!!.findNavController().navigate(direction)
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
