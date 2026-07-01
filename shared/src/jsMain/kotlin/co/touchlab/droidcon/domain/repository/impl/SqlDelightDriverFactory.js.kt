package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import app.cash.sqldelight.driver.worker.expected.Worker
import co.touchlab.droidcon.util.TimezoneInit

@Suppress("unused")
private val ensureTimezoneDataLoaded = TimezoneInit

actual class SqlDelightDriverFactory {
    actual fun createDriver(): SqlDriver = WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""),
        ),
    )
}
