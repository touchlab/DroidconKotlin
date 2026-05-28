package co.touchlab.droidcon.test

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual suspend fun createInMemoryDriver(): SqlDriver {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    DroidconDatabase.Schema.create(driver).await()
    return driver
}
