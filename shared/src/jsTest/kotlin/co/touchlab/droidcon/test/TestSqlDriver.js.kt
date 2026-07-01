package co.touchlab.droidcon.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import app.cash.sqldelight.driver.worker.expected.Worker
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.util.TimezoneInit

@Suppress("unused")
private val ensureTimezoneDataLoaded = TimezoneInit

actual suspend fun createInMemoryDriver(): SqlDriver {
    val driver = WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""),
        ),
    )
    DroidconDatabase.Schema.create(driver).await()
    return driver
}
