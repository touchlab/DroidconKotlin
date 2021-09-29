package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.DroidconDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(DroidconDatabase.Schema, "new-droidcon.db")
    }
}