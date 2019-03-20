package co.touchlab.sessionize

import co.touchlab.droidcon.db.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.runBlocking

actual fun testDbConnection(): SqlDriver {
    //AndroidSqliteDriver(Database.Schema, this, "droidcondb")
    val driver = JdbcSqliteDriver()
    Database.Schema.create(driver)
    return driver
}

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }