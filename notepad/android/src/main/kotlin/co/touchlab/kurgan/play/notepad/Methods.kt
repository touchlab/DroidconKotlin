package co.touchlab.kurgan.play.notepad

import java.util.concurrent.*
import co.touchlab.kurgan.architecture.database.sqldelight.SqlDelightDatabaseHelper

actual fun memzy(body: () -> Unit) = body()

private val executorService = Executors.newSingleThreadExecutor()
private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())


actual fun runOnBackground(workerData: WorkerData){
    executorService.execute {
        r.run()
    }
}

actual fun createHolder(db: SqlDelightDatabaseHelper)= HelperHolder(db)

/*
actual fun runOnMain(r:Runner){
    mainHandler.post {
        r.run()
    }
}*/
