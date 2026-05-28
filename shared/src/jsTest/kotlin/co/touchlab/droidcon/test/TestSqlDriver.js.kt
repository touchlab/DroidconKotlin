package co.touchlab.droidcon.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import co.touchlab.droidcon.db.DroidconDatabase
import app.cash.sqldelight.driver.worker.expected.Worker
actual suspend fun createInMemoryDriver(): SqlDriver {
    val driver = WebWorkerDriver(
        Worker(
            js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""),
        ),
    )
    DroidconDatabase.Schema.create(driver).await()
    return driver
}
