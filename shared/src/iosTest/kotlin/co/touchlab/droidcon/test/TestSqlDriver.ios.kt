package co.touchlab.droidcon.test

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual suspend fun createInMemoryDriver(): SqlDriver = NativeSqliteDriver(
    schema = DroidconDatabase.Schema.synchronous(),
    name = "test_${kotlin.random.Random.nextLong()}",
)
