package co.touchlab.sessionize.sponsors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.SponsorSessionModel
import co.touchlab.sessionize.databinding.FragmentSponsorBinding
import co.touchlab.sessionize.databinding.ItemSponsorGroupBinding
import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.sponsorClicked
import co.touchlab.sessionize.util.viewBindingLifecycle
import com.squareup.picasso.Picasso


class SponsorsFragment : Fragment() {

    private var binding by viewBindingLifecycle<FragmentSponsorBinding>()

    lateinit var adapter: SponsorGroupAdapter
    lateinit var sponsorViewModel:SponsorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sponsorViewModel = SponsorViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSponsorBinding.inflate(inflater, container, false)

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
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    inner class SponsorGroupAdapter : RecyclerView.Adapter<SponsorGroupViewHolder>() {
        var sponsorGroupItems: List<SponsorGroup> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorGroupViewHolder {
            return SponsorGroupViewHolder(ItemSponsorGroupBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int = sponsorGroupItems.size

        override fun onBindViewHolder(holder: SponsorGroupViewHolder, position: Int) {
            val sponsorGroup = sponsorGroupItems[position]
            holder.binding.groupName.text = sponsorGroup.groupName
            holder.binding.flowGroup.removeAllViews()
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
                val iv = layoutInflater.inflate(itemLayout, holder.binding.flowGroup, false) as ImageView
                Picasso.get().load(sponsor.icon).into(iv)
                holder.binding.flowGroup.addView(iv)
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

    class SponsorGroupViewHolder(val binding: ItemSponsorGroupBinding) : RecyclerView.ViewHolder(binding.root)
}
