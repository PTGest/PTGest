package pt.isel.leic.ptgest.http.utils

object Uris {
    private const val BASE_URI = "/api"

    const val AUTH_PREFIX = "$BASE_URI/auth"

    object Auth {
        private const val SIGNUP_PREFIX = "/signup"

        const val SIGNUP_TRAINEE = "$SIGNUP_PREFIX/trainee"
        const val SIGNUP_INDEPENDENT_TRAINER = "$SIGNUP_PREFIX/itrainer"
        const val SIGNUP_HIRED_TRAINER = "$SIGNUP_PREFIX/htrainer"
        const val SIGNUP_COMPANY = "$SIGNUP_PREFIX/company"

        const val LOGIN = "$AUTH_PREFIX/login"
        const val LOGOUT = "$AUTH_PREFIX/logout"
        const val VALIDATE_TOKEN = "$AUTH_PREFIX/validate"
    }
}