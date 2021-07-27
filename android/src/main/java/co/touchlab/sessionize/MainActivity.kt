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
import co.touchlab.sessionize.feedback.FeedbackManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MainActivity : AppCompatActivity(), SnackHost, KoinComponent {

    val feedbackManager:FeedbackManager by inject()
    val firebaseMessageHandler = FirebaseMessageHandler()

    var scheduleRecyclerViewPos: Parcelable? = null
    var scheduleTabPos:Int = 0

    var agendaRecyclerViewPos:Parcelable? = null
    var agendaTabPos:Int = 0
    val networkRepo: NetworkRepo by inject()

    override fun showSnack(message: String, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnack(message: Int, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_schedule,
                R.id.navigation_my_agenda,
                R.id.navigation_sponsors,
                R.id.navigation_settings)) // Destinations where the back arrow doesn't appear
        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration)
        navigation.setupWithNavController(navController)
    }


    override fun onResume() {
        super.onResume()
        networkRepo.refreshData()

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
