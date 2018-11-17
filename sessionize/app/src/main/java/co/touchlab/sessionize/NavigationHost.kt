package co.touchlab.sessionize

import android.support.annotation.AnimRes

import androidx.fragment.app.Fragment

/**
 * A host (typically an `Activity`} that can display fragments and knows how to respond to
 * navigation events.
 */
interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    fun navigateTo(
            fragment: Fragment,
            addToBackstack: Boolean)

    /**
     * Trigger an animated navigation to the specified fragment,
     * optionally adding a transaction to the back stack to make this navigation reversible.
     */
    fun navigateTo(
            fragment: Fragment,
            addToBackstack: Boolean,
            @AnimRes enterAnim: Int,
            @AnimRes exitAnim: Int,
            @AnimRes popEnterAnim: Int,
            @AnimRes popExitAnim: Int)
}