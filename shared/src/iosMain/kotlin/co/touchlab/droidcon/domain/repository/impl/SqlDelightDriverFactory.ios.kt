package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.DroidconDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(DroidconDatabase.Schema, "droidcon2023.db")
    }
}
