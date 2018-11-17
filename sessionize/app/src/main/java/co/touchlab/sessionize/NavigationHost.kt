package co.touchlab.sessionize

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
            addToBackstack: Boolean,
            fragmentAnimation: FragmentAnimation? = null)
}

data class FragmentAnimation(
        val enterAnim: Int,
        val exitAnim: Int,
        val popEnterAnim: Int,
        val popExitAnim: Int
)