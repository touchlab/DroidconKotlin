package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(DroidconDatabase.Schema, "droidcon2024.db")
}
