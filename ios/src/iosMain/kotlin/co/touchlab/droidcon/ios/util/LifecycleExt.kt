package co.touchlab.droidcon.ios.util

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume as resumeLifecycle
import com.arkivanov.essenty.lifecycle.stop as stopLifecycle

fun LifecycleRegistry.resume() {
    resumeLifecycle()
}

fun LifecycleRegistry.stop() {
    stopLifecycle()
}
