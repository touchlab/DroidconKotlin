package co.touchlab.sessionize.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.touchlab.sessionize.R
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.databinding.FragmentSettingsBinding
import co.touchlab.sessionize.feedback.FeedbackManager.Companion.FeedbackDisabledNotificationName
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.util.viewBindingLifecycle


class SettingsFragment : Fragment() {

    private var binding by viewBindingLifecycle<FragmentSettingsBinding>()

    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = {
        SettingsViewModelFactory()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val adapter = SettingsAdapter(requireActivity())
        binding.recycler.adapter = adapter
        updateContent()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
        updateContent()
    }

    private fun updateContent() {
        val adapter = SettingsAdapter(requireActivity())

        //SettingsModel.loadSettingsInfo {
        adapter.addSwitchRow("Enable Feedback",
                R.drawable.baseline_feedback_24,
                ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED, true)
        ) { _, isChecked ->
            settingsViewModel.settingsModel.setFeedbackSettingEnabled(isChecked)
        }
        adapter.addSwitchRow("Enable Reminders",
                R.drawable.baseline_insert_invitation_24,
                ServiceRegistry.appSettings.getBoolean(REMINDERS_ENABLED, true)
        ) { _, isChecked ->
            settingsViewModel.settingsModel.setRemindersSettingEnabled(isChecked)
        }
        adapter.addButtonRow("About", R.drawable.menu_info) {
            val direction = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
            view!!.findNavController().navigate(direction)
        }
        //}

        binding.recycler.adapter = adapter
    }
}
