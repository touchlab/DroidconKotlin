package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.drivers.native.NativeSqliteDriver
import app.cash.sqldelight.drivers.native.wrapConnection
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.sqliter.DatabaseConfiguration

internal actual fun testDbConnection(): SqlDriver {
    val schema = DroidconDatabase.Schema
    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = "new-droidcon.db",
            version = schema.version,
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion, newVersion) }
            },
            inMemory = true
        )
    )
}
