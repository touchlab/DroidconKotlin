package co.touchlab.sessionize

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import co.touchlab.droidcon.db.DroidconDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking

actual fun testDbConnection(): SqlDriver {
    val app = ApplicationProvider.getApplicationContext<Application>()
    return AndroidSqliteDriver(DroidconDb.Schema, app, "droidcondb")
}

actual fun <T> runTest(block: suspend () -> T) { runBlocking { block() } }