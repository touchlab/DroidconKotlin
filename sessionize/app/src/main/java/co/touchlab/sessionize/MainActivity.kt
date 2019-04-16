package co.touchlab.sessionize

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import co.touchlab.sessionize.about.AboutFragment
import co.touchlab.sessionize.schedule.ScheduleFragment
import co.touchlab.sessionize.sponsors.SponsorsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuItem
import co.touchlab.sessionize.settings.SettingsFragment


class MainActivity : AppCompatActivity(), NavigationHost, SnackHost {
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
            R.id.navigation_about -> navigateTo(AboutFragment(), false)
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

        settingsButton.setOnClickListener {
            settingsButtonPressed()
        }
        if(savedInstanceState == null) {
            navigateTo(ScheduleFragment.newInstance(true), false)
        }
    }

    override fun onResume() {
        super.onResume()
        AppContext.refreshData()
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

    private fun settingsButtonPressed(){
            (this as NavigationHost).navigateTo(
                    SettingsFragment.newInstance(),
                    true,
                    FragmentAnimation(R.anim.slide_from_right,
                            R.anim.slide_to_left,
                            R.anim.slide_from_left,
                            R.anim.slide_to_right)
            )
    }
}
