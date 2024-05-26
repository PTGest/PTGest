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
        const val VALIDATE_REFRESH_TOKEN = "/auth/validate-refresh-token"
        const val LOGOUT = "/auth/logout"
    }

    object User {
        const val PROFILE = "/profile"
    }

    object Company {
        const val PREFIX = "${Uris.PREFIX}/company"
        const val COMPANY_TRAINERS = "/trainers"
        const val COMPANY_TRAINEES = "/trainees"
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

    object Workout {
        const val CREATE_CUSTOM_EXERCISE = "/custom-exercise"
        const val GET_EXERCISES = "/exercises"
        const val GET_EXERCISE_DETAILS = "/exercise/{exerciseId}"

        const val CREATE_CUSTOM_SET = "/custom-set"
        const val GET_SETS = "/sets"
        const val GET_SET_DETAILS = "/set/{setId}"

        const val CREATE_CUSTOM_WORKOUT = "/custom-workout"
        const val GET_WORKOUTS = "/workouts"
        const val GET_WORKOUT_DETAILS = "/workout/{workoutId}"
    }

    object Session {
        const val CREATE_SESSION = "/session"
        const val GET_SESSIONS = "/sessions"
        const val GET_SESSION_DETAILS = "/session/{sessionId}"
        const val CHANGE_SESSION_DATE = "/session/{sessionId}/change-date"
        const val EDIT_SESSION = "/session/{sessionId}/edit"
        const val CANCEL_SESSION = "/session/{sessionId}/cancel"
    }
}
