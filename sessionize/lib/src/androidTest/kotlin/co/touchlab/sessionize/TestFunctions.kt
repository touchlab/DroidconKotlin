package co.touchlab.sessionize

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.runBlocking

actual fun testDbConnection(): SqlDriver = JdbcSqliteDriver()

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }