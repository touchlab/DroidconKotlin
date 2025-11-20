package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.db.SqlDriver

// Note: SQLDelight doesn't have native wasmJs support yet
// This is a placeholder implementation
actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver = throw UnsupportedOperationException("SQLDelight driver is not yet supported for wasmJs target")
}
