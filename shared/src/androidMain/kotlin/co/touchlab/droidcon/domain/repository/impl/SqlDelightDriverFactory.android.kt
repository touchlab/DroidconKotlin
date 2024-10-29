package co.touchlab.droidcon.domain.repository.impl

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual class SqlDelightDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(DroidconDatabase.Schema, context, "new-droidcon2024.db")
    }
}
