package co.touchlab.notepad

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.touchlab.notepad.schedule.ScheduleFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationHost, SnackHost {
    override fun showSnack(message: String, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSnack(message: Int, length: Int) {
        Snackbar.make(findViewById<View>(R.id.navigation), message, Snackbar.LENGTH_SHORT).show()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_schedule -> {
                navigateTo(ScheduleFragment.newInstance(true), false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_my_agenda -> {
                navigateTo(ScheduleFragment.newInstance(false), false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sponsors -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        navigateTo(ScheduleFragment.newInstance(true), false)
    }

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
