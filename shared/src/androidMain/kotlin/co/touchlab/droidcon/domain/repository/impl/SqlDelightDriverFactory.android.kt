package co.touchlab.droidcon.domain.repository.impl

import android.content.Context
import co.touchlab.droidcon.db.DroidconDatabase
import app.cash.sqldelight.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver

actual class SqlDelightDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(DroidconDatabase.Schema, context, "new-droidcon2023.db")
    }
}
