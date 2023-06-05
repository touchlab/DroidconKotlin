package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.sqliter.DatabaseFileContext
import co.touchlab.sqliter.DatabaseManager
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver {
        DatabaseFileContext.databasePath("mydb", null)
        return NativeSqliteDriver(DroidconDatabase.Schema, "droidcon2023.db")
    }
}
