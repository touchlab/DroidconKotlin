package co.touchlab.droidcon.domain.repository.impl

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual class SqlDelightDriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = DroidconDatabase.Schema.synchronous(),
        context = context,
        name = "droidcon.db",
    )
}
