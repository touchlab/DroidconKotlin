package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.sqliter.DatabaseConfiguration

internal actual fun testDbConnection(): SqlDriver {
    val schema = DroidconDatabase.Schema
    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = "new-droidcon.db",
            version = if (schema.version > Int.MAX_VALUE) {
                error("Schema version is larger than Int.MAX_VALUE: ${schema.version}.")
            } else {
                schema.version.toInt()
            },
            create = { connection ->
                wrapConnection(connection) { schema.create(it) }
            },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) {
                    schema.migrate(it, oldVersion.toLong(), newVersion.toLong())
                }
            },
            inMemory = true
        )
    )
}
