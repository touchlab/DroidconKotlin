package co.touchlab.sessionize

import co.touchlab.sessionize.platform.defaultDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking

actual fun testDbConnection(): SqlDriver = defaultDriver()

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }