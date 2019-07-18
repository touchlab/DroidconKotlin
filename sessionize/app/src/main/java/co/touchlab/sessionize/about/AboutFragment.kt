package co.touchlab.sessionize.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.AboutInfo
import co.touchlab.sessionize.AboutModel

import co.touchlab.sessionize.R
import co.touchlab.sessionize.speaker.finDrawableId

class AboutFragment : Fragment() {

    lateinit var recycler:RecyclerView
    lateinit var adapter:AboutInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        recycler = view.findViewById(R.id.recycler)
        adapter = AboutInfoAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(activity)
        AboutModel.loadAboutInfo {
            adapter.updateDate(it)
        }
        return view
    }

    inner class AboutInfoAdapter : RecyclerView.Adapter<AboutInfoViewHolder>() {

        var infoList:List<AboutInfo> = emptyList()

        fun updateDate(data:List<AboutInfo>){
            infoList = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutInfoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_about_info, parent, false)

            return AboutInfoViewHolder(view)
        }

        override fun getItemCount(): Int = infoList.size

        override fun onBindViewHolder(holder: AboutInfoViewHolder, position: Int) {
            val aboutInfo = infoList.get(position)
            holder.aboutTextBody.visibility = View.VISIBLE
            holder.aboutBinkyBody.visibility = View.GONE
            holder.header.text = aboutInfo.title
            holder.body.text = aboutInfo.detail
            if(!aboutInfo.icon.isNullOrBlank()) {
                holder.logo.setImageResource(finDrawableId(requireContext(), aboutInfo.icon))
                holder.logo.visibility = View.VISIBLE
            }else{
                holder.logo.visibility = View.GONE
            }

        }

    }

    class AboutInfoViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val aboutTextBody:ViewGroup = itemView.findViewById(R.id.aboutTextBody)
        val aboutBinkyBody:View = itemView.findViewById(R.id.aboutBinkyBody)
        val header = itemView.findViewById<TextView>(R.id.header)
        val body = itemView.findViewById<TextView>(R.id.body)
        val logo = itemView.findViewById<ImageView>(R.id.logo)
    }
}

