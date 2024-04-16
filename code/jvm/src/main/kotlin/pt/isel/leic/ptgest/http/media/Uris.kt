package pt.isel.leic.ptgest.http.media

object Uris {
    const val PREFIX = "/api"

    object Auth {
        const val SIGNUP = "/signup"
        const val AUTHENTICATED_SIGNUP = "/auth/signup"
        const val FORGET_PASSWORD = "/forget-password"
        const val VALIDATE_PASSWORD_RESET_TOKEN = "/validate-password-reset-token/{token}"
        const val RESET_PASSWORD = "/reset-password/{token}"
        const val LOGIN = "/login"
        const val REFRESH = "/auth/refresh"
        const val LOGOUT = "/auth/logout"
    }

    object User {
        const val PROFILE = "/profile"
        const val USER_DETAILS = "/user/{id}"

        fun userDetails(id: Int) = "/$PREFIX/user/$id"
    }

    object Company {
        const val COMPANY_TRAINERS = "/company/trainers"
        const val ASSIGN_TRAINER_TRAINEE = "/trainer/{trainerId}/assign-trainee/{studentId}"
        const val REASSIGN_TRAINER = "/trainer/{trainerId}/reassign-trainee/{studentId}"
    }

    object Trainer

    object Trainee
}
