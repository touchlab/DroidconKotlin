package co.touchlab.sessionize.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.feedback.FeedbackManager.Companion.FeedbackDisabledNotificationName
import co.touchlab.sessionize.platform.AndroidAppContext


class SettingsFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var settingsViewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProviders.of(this, SettingsViewModelFactory())[SettingsViewModel::class.java]

        LocalBroadcastManager.getInstance(AndroidAppContext.app).registerReceiver(changedSettingReciever,
                IntentFilter(FeedbackDisabledNotificationName))
    }

    private val changedSettingReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateContent()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(AndroidAppContext.app).unregisterReceiver(changedSettingReciever)
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        recycler = view.findViewById(R.id.recycler)

        val adapter = SettingsAdapter(activity!!)
        recycler.adapter = adapter
        updateContent()

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
        adapter.addButtonRow("About", R.drawable.menu_info, View.OnClickListener {
            val direction = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
            view!!.findNavController().navigate(direction)
        })
        //}

        recycler.adapter = adapter
    }
}
