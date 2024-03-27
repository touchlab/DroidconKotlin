package co.touchlab.droidcon

data class UserContext(
    val isAuthenticated: Boolean,
    val userData: UserData?,
)

data class UserData(
    val id: String,
    val name: String?,
    val email: String?,
    val pictureUrl: String?,
)
