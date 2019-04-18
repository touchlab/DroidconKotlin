package co.touchlab.sessionize.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.AppContext.FEEDBACK_ENABLED
import co.touchlab.sessionize.AppContext.REMINDERS_ENABLED
import co.touchlab.sessionize.R
import co.touchlab.sessionize.ServiceRegistry

class SettingsFragment : Fragment() {

    lateinit var recycler: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        recycler = view.findViewById(R.id.recycler)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(getActivity())

        updateContent()
    }

    private fun updateContent() {
        val adapter = SettingsAdapter(activity!!)


        adapter.addSwitchRow("Enable Feedback",
                R.drawable.baseline_feedback_24,
                ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED)
        )
        adapter.addSwitchRow("Enable Reminders",
                R.drawable.baseline_insert_invitation_24,
                ServiceRegistry.appSettings.getBoolean(REMINDERS_ENABLED)
        )

        recycler.adapter = adapter
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
