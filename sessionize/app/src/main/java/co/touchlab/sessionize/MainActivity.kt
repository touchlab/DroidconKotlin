package co.touchlab.sessionize

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.feedback.FeedbackManager
import co.touchlab.sessionize.schedule.ScheduleFragment
import co.touchlab.sessionize.settings.SettingsFragment
import co.touchlab.sessionize.sponsors.SponsorsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SnackHost {

    private var feedbackManager = FeedbackManager()
    val firebaseMessageHandler = FirebaseMessageHandler()

    override fun showSnack(message: String, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnack(message: Int, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener {
            val name = topFragmentName
            if(name == "") {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = findApplicationName(this)
            }else{
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                if(name.contains("Speaker"))
                    supportActionBar?.title = "Speaker"
                else if(name.contains("Event"))
                    supportActionBar?.title = "Session"
            }
        }

            val navController = findNavController(R.id.nav_host_fragment)
            navigation.setupWithNavController(navController)

        //setSupportActionBar(findViewById(R.id.app_bar) as Toolbar)
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

    private val topFragmentName:String
    get() {
        val index = supportFragmentManager.backStackEntryCount - 1
        if(index < 0)
            return ""
        val backEntry = supportFragmentManager.getBackStackEntryAt(index)

        return backEntry.name?:""
    }
}
