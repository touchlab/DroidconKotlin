package co.touchlab.notepad.utils

import co.touchlab.multiplatform.architecture.db.sqlite.AndroidNativeOpenHelperFactory
import co.touchlab.multiplatform.architecture.db.sqlite.NativeOpenHelperFactory
import com.google.codelabs.mdc.kotlin.shrine.application.ShrineApplication

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {}
actual fun backgroundTask(backJob: () -> Unit) {}

actual fun initContext(): NativeOpenHelperFactory = AndroidNativeOpenHelperFactory(ShrineApplication.instance)

actual fun sleepThread(millis: Long) {}
actual fun <T> goFreeze(a: T): T = a