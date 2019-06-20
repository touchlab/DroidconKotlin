package co.touchlab.sessionize.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.FragmentAnimation
import co.touchlab.sessionize.NavigationHost
import co.touchlab.sessionize.R
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.about.AboutFragment

class SettingsFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var settingsViewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        settingsViewModel = ViewModelProviders.of(this, SettingsViewModelFactory())[SettingsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        recycler = view.findViewById(R.id.recycler)

        val adapter = SettingsAdapter(activity!!)
        recycler.adapter = adapter

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

        //SettingsModel.loadSettingsInfo {
            adapter.addSwitchRow("Enable Feedback",
                    R.drawable.baseline_feedback_24,
                    ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED, true),
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        settingsViewModel.settingsModel.setFeedbackSettingEnabled(isChecked)
                    }
            )
            adapter.addSwitchRow("Enable Reminders",
                    R.drawable.baseline_insert_invitation_24,
                    ServiceRegistry.appSettings.getBoolean(REMINDERS_ENABLED, true),
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        settingsViewModel.settingsModel.setRemindersSettingEnabled(isChecked)
                    }
            )
            adapter.addButtonRow("About",R.drawable.menu_info, View.OnClickListener {
                (activity as NavigationHost).navigateTo(
                        AboutFragment.newInstance(),
                        true,
                        FragmentAnimation(R.anim.slide_from_right,
                                R.anim.slide_to_left,
                                R.anim.slide_from_left,
                                R.anim.slide_to_right)
                )
            })
        //}

        recycler.adapter = adapter
    }
}
