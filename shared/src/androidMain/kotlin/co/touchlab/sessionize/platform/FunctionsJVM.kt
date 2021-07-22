package co.touchlab.sessionize.platform

import java.net.URL
import java.util.*


actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun createUuid(): String = UUID.randomUUID().toString()

actual fun simpleGet(url: String): String = URL(url).readText()
