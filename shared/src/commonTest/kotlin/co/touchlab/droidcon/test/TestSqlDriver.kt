package co.touchlab.droidcon.test

import app.cash.sqldelight.db.SqlDriver

expect suspend fun createInMemoryDriver(): SqlDriver
