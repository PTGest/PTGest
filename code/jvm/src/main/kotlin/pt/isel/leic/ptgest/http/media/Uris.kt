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

        fun userDetails(id: Int) = "/$PREFIX/user/$id"
    }

    object Company {
        const val PREFIX = "${Uris.PREFIX}/company"
        const val COMPANY_TRAINERS = "/trainers"
        const val ASSIGN_TRAINER = "/trainee/{traineeId}/assign-trainer"
        const val REASSIGN_TRAINER = "/trainee/{traineeId}/reassign-trainer"
        const val UPDATE_TRAINER_CAPACITY = "/trainer/{trainerId}/update-capacity"
        const val REMOVE_TRAINER = "/trainer/{trainerId}"
    }

    object Trainer {
        const val PREFIX = "${Uris.PREFIX}/trainer"
    }

    object Trainee {
        const val PREFIX = "${Uris.PREFIX}/trainee"
    }
}
