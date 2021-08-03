package co.touchlab.droidcon.application.service

interface NotificationSchedulingService {
    suspend fun runScheduling()
}