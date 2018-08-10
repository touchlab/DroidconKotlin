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
import co.touchlab.sessionize.SponsorModel
import co.touchlab.sessionize.jsondata.SponsorGroup
import com.nex3z.flowlayout.FlowLayout
import com.squareup.picasso.Picasso
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class SponsorsFragment : Fragment() {

    lateinit var adapter: SponsorGroupAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sponsor, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)

        adapter = SponsorGroupAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        SponsorModel.loadSponsor {
            adapter.sponsorGroups = it
            adapter.notifyDataSetChanged()
        }

        return view
    }

    inner class SponsorGroupAdapter : RecyclerView.Adapter<SponsorGroupViewHolder>(){
        var sponsorGroups:List<SponsorGroup> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorGroupViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sponsor_group, parent, false)

            return SponsorGroupViewHolder(view)
        }

        override fun getItemCount(): Int = sponsorGroups.size

        override fun onBindViewHolder(holder: SponsorGroupViewHolder, position: Int) {
            val sponsorGroup = sponsorGroups.get(position)
            holder.groupName.text = sponsorGroup.groupName
            holder.flowGroup.removeAllViews()
            val layoutInflater = LayoutInflater.from(activity)
            val itemLayout = if(sponsorGroup.groupName.contains("Gold")){R.layout.item_sponsor_pic}else{R.layout.item_sponsor_pic_small}
            for (sponsor in sponsorGroup.sponsors) {
                val iv = layoutInflater.inflate(itemLayout, holder.flowGroup, false) as ImageView
                Picasso.get().load(sponsor.icon).into(iv)
                holder.flowGroup.addView(iv)
                iv.setOnClickListener {
                    if(!sponsor.url.isBlank()) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(sponsor.url)
                        startActivity(i)
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
