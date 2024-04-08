package pt.isel.leic.ptgest.http.utils

object Uris {
    const val PREFIX = "/api"

    object Auth {
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val REFRESH = "/auth/refresh"
        const val AUTHENTICATED_SIGNUP = "/auth/signup"
        const val LOGOUT = "/auth/logout"
    }
}
