package pt.isel.leic.ptgest.http.utils

object Uris {
    const val PREFIX = "/api"

    object Auth {
        const val SIGNUP = "/signup"
        const val AUTHENTICATED_SIGNUP = "/auth/signup"
        const val FORGET_PASSWORD = "/forget-password"
        const val VALIDATE_PASSWORD_RESET_TOKEN = "/validate-password-reset-token"
        const val RESET_PASSWORD = "/reset-password"
        const val LOGIN = "/login"
        const val REFRESH = "/auth/refresh"
        const val LOGOUT = "/auth/logout"
    }
}
