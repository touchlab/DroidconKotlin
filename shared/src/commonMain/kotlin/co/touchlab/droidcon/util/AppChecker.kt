package co.touchlab.droidcon.util

import co.touchlab.droidcon.Constants
import com.soywiz.krypto.md5

object AppChecker {

    /**
     * Checks the generated MD5 hash against the hash saved in Constants.
     */
    @Throws(IllegalStateException::class)
    fun checkTimeZoneHash() {
        check(Constants.conferenceTimeZoneHash == toMD5("${Constants.conferenceTimeZone.id}|${Constants.sessionizeScheduleId}")) {
            "TimeZone hash is incorrect."
        }
    }

    private fun toMD5(text: String): String {
        return text.encodeToByteArray().md5().hex
    }
}
