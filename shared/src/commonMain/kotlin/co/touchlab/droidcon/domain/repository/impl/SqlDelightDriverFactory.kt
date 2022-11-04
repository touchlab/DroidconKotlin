package co.touchlab.droidcon.domain.repository.impl

import com.squareup.sqldelight.db.SqlDriver

expect class SqlDelightDriverFactory {
    fun createDriver(): SqlDriver
}
