package co.touchlab.droidcon.domain.repository.impl

import android.content.Context
import co.touchlab.droidcon.db.DroidconDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class SqlDelightDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(DroidconDatabase.Schema, context, "droidcon.db")
    }
}