package co.touchlab.sessionize.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.AboutInfo
import co.touchlab.sessionize.AboutModel
import co.touchlab.sessionize.databinding.FragmentAboutBinding
import co.touchlab.sessionize.databinding.ItemAboutInfoBinding
import co.touchlab.sessionize.speaker.finDrawableId
import co.touchlab.sessionize.util.viewBindingLifecycle

class AboutFragment : Fragment() {

    private var binding by viewBindingLifecycle<FragmentAboutBinding>()

    lateinit var adapter:AboutInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.recycler.apply {
            adapter = AboutInfoAdapter()
            adapter = adapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        AboutModel.loadAboutInfo {
            adapter.updateDate(it)
        }
        return binding.root
    }

    inner class AboutInfoAdapter : RecyclerView.Adapter<AboutInfoViewHolder>() {

        var infoList:List<AboutInfo> = emptyList()

        fun updateDate(data:List<AboutInfo>){
            infoList = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutInfoViewHolder {
            val binding = ItemAboutInfoBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            return AboutInfoViewHolder(binding)
        }

        override fun getItemCount(): Int = infoList.size

        override fun onBindViewHolder(holder: AboutInfoViewHolder, position: Int) {
            holder.binding.run {
                val aboutInfo = infoList[position]
                aboutTextBody.visibility = View.VISIBLE
                aboutBinkyBody.visibility = View.GONE
                header.text = aboutInfo.title
                body.text = aboutInfo.detail
                if(!aboutInfo.icon.isNullOrBlank()) {
                    logo.setImageResource(finDrawableId(requireContext(), aboutInfo.icon))
                    logo.visibility = View.VISIBLE
                }else{
                    logo.visibility = View.GONE
                }
            }
        }

    }

    class AboutInfoViewHolder(val binding: ItemAboutInfoBinding):RecyclerView.ViewHolder(binding.root)
}

