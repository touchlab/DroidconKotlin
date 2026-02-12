package co.touchlab.droidcon.domain.repository.impl

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.touchlab.droidcon.db.DroidconDatabase

actual class SqlDelightDriverFactory(private val context: Context) {
    
    @Suppress("UNCHECKED_CAST")
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = DroidconDatabase.Schema as SqlSchema<QueryResult.Value<Unit>>,
        context = context,
        name = "droidcon.db",
    )
}
