package co.touchlab.sessionize

import com.squareup.sqldelight.db.SqlDriver

expect fun testDbConnection(): SqlDriver

expect fun <T> runTest(block: suspend () -> T)