package pt.isel.leic.ptgest.http.utils

object Uris {
    private const val BASE_URI = "/api"

    const val AUTH_PREFIX = "$BASE_URI/auth"

    object Auth {
        const val SIGNUP = "/signup"
        const val LOGIN = "$AUTH_PREFIX/login"
        const val LOGOUT = "$AUTH_PREFIX/logout"
        const val VALIDATE_TOKEN = "$AUTH_PREFIX/validate"
    }
}