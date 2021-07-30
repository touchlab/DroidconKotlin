package co.touchlab.droidcon.domain.service

import kotlinx.datetime.LocalDateTime

interface DateTimeService {
    fun now(): LocalDateTime
}