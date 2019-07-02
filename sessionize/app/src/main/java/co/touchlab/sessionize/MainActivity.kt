package co.touchlab.sessionize

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.feedback.FeedbackManager
import co.touchlab.sessionize.schedule.ScheduleFragment
import co.touchlab.sessionize.settings.SettingsFragment
import co.touchlab.sessionize.sponsors.SponsorsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationHost, SnackHost {

    private var feedbackManager = FeedbackManager()
    val firebaseMessageHandler = FirebaseMessageHandler()

    override fun showSnack(message: String, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnack(message: Int, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_schedule -> navigateTo(ScheduleFragment.newInstance(true), false)
            R.id.navigation_my_agenda -> navigateTo(ScheduleFragment.newInstance(false), false)
            R.id.navigation_sponsors -> navigateTo(SponsorsFragment(), false)
            R.id.navigation_settings -> navigateTo(SettingsFragment(), false)
            else -> return@OnNavigationItemSelectedListener false
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

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

        setSupportActionBar(findViewById(R.id.app_bar) as Toolbar)
        if(savedInstanceState == null) {
            navigateTo(ScheduleFragment.newInstance(true), false)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun navigateTo(
            fragment: Fragment,
            addToBackstack: Boolean,
            fragmentAnimation: FragmentAnimation?) {

        if(!addToBackstack){
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        val transaction = supportFragmentManager
                .beginTransaction()

        if(fragmentAnimation != null){
            transaction.setCustomAnimations(
                    fragmentAnimation.enterAnim,
                    fragmentAnimation.exitAnim,
                    fragmentAnimation.popEnterAnim,
                    fragmentAnimation.popExitAnim)
        }

        if (addToBackstack) {
            transaction.add(R.id.container, fragment)
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }else{
            transaction.replace(R.id.container, fragment)
        }

        transaction.commit()
    }
}
