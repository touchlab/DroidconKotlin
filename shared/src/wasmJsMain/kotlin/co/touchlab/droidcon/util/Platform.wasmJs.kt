package co.touchlab.droidcon.util

internal actual fun printThrowable(t: Throwable) {
    // For wasmJs, use println which will output to console
    println("ERROR: $t")
    println("STACK: ${t.stackTraceToString()}")
}
