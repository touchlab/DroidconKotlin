package co.touchlab.sessionize

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.databinding.ActivityMainBinding
import co.touchlab.sessionize.feedback.FeedbackManager
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), SnackHost {

    private lateinit var binding: ActivityMainBinding

    private var feedbackManager = FeedbackManager()
    val firebaseMessageHandler = FirebaseMessageHandler()

    var scheduleRecyclerViewPos: Parcelable? = null
    var scheduleTabPos:Int = 0

    var agendaRecyclerViewPos:Parcelable? = null
    var agendaTabPos:Int = 0


    override fun showSnack(message: String, length: Int) {
        Snackbar.make(binding.navigation, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnack(message: Int, length: Int) {
        Snackbar.make(binding.navigation, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBar)


        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_schedule,
                R.id.navigation_my_agenda,
                R.id.navigation_sponsors,
                R.id.navigation_settings)) // Destinations where the back arrow doesn't appear
        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)
    }


    override fun onResume() {
        super.onResume()
        NetworkRepo.refreshData()

        feedbackManager.setFragmentManager(supportFragmentManager)
        feedbackManager.showFeedbackForPastSessions()
    }

    override fun onDestroy() {
        super.onDestroy()
        feedbackManager.close()
    }
    private fun findApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}
