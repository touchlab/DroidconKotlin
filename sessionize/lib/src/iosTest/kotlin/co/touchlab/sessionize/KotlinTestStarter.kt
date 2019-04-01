package co.touchlab.sessionize

import kotlin.native.internal.test.testLauncherEntryPoint

fun kickOffTest():Int {

    return testLauncherEntryPoint(emptyArray<String>())
}

