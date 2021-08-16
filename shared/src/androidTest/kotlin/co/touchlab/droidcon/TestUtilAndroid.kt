package co.touchlab.droidcon

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import co.touchlab.droidcon.db.DroidconDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

internal actual fun testDbConnection(): SqlDriver {
    val app = ApplicationProvider.getApplicationContext<Application>()
    return AndroidSqliteDriver(DroidconDatabase.Schema, app, "new-droidcon.db")
}
