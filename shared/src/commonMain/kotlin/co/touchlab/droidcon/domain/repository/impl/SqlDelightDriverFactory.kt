package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.db.SqlDriver

expect class SqlDelightDriverFactory {
    fun createDriver(): SqlDriver
}
