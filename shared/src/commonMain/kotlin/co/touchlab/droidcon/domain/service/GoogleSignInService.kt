package co.touchlab.droidcon.domain.service

interface GoogleSignInService {
    fun performGoogleLogin(): Boolean
    fun performGoogleLogout(): Boolean
}
