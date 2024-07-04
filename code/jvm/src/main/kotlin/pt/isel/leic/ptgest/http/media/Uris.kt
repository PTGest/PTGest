package pt.isel.leic.ptgest.http.media

object Uris {
    const val PREFIX = "/api"

    object Auth {
        const val SIGNUP = "/signup"
        const val AUTHENTICATED_SIGNUP = "/auth/signup"
        const val FORGET_PASSWORD = "/forget-password"
        const val VALIDATE_PASSWORD_RESET_TOKEN = "/validate-password-reset-token/{token}"
        const val RESET_PASSWORD = "/reset-password/{requestToken}"
        const val LOGIN = "/login"
        const val REFRESH = "/auth/refresh"
        const val VALIDATE_AUTHENTICATION = "/auth/validate"
        const val LOGOUT = "/auth/logout"
    }

    object User {
        const val USER_DETAILS = "user/{id}"
    }

    object Company {
        const val PREFIX = "${Uris.PREFIX}/company"
        const val TRAINERS = "/trainers"
        const val TRAINEES = "/trainees"
        const val ASSIGN_TRAINER = "/trainee/{traineeId}/assign-trainer"
        const val REASSIGN_TRAINER = "/trainee/{traineeId}/reassign-trainer"
        const val UPDATE_TRAINER_CAPACITY = "/trainer/{trainerId}/update-capacity"
        const val REMOVE_TRAINER = "/trainer/{trainerId}"
    }

    object Trainer {
        const val PREFIX = "${Uris.PREFIX}/trainer"
        const val TRAINEES = "/trainees"
    }

    object Trainee {
        const val PREFIX = "${Uris.PREFIX}/trainee"

        const val GET_REPORTS = "/reports"
        const val GET_REPORT_DETAILS = "/report/{reportId}"

        const val GET_TRAINEE_DATA_HISTORY = "/data"
        const val GET_TRAINEE_DATA_DETAILS = "/data/{dataId}"
    }

    object Report {
        private const val PREFIX = "/trainee/{traineeId}"
        const val CREATE_REPORT = "$PREFIX/report"
        const val GET_REPORTS = "$PREFIX/reports"
        const val GET_REPORT_DETAILS = "$PREFIX/report/{reportId}"
        const val EDIT_REPORT = "$PREFIX/report/{reportId}/edit"
        const val DELETE_REPORT = "$PREFIX/report/{reportId}/delete"
    }

    object TraineeData {
        private const val PREFIX = "/trainee/{traineeId}"
        const val ADD_TRAINEE_DATA = "$PREFIX/data"
        const val GET_TRAINEE_DATA_HISTORY = "$PREFIX/data"
        const val GET_TRAINEE_DATA_DETAILS = "$PREFIX/data/{dataId}"
    }

    object Exercise {
        const val CREATE_CUSTOM_EXERCISE = "/custom-exercise"
        const val GET_EXERCISES = "/exercises"
        const val GET_EXERCISE_DETAILS = "/exercise/{exerciseId}"
        const val FAVORITE_EXERCISE = "/exercise/{exerciseId}/favorite"
        const val UNFAVORITE_EXERCISE = "/exercise/{exerciseId}/unfavorite"
    }

    object Set {
        const val CREATE_CUSTOM_SET = "/custom-set"
        const val GET_SETS = "/sets"
        const val GET_SET_DETAILS = "/set/{setId}"
        const val FAVORITE_SET = "/set/{setId}/favorite"
        const val UNFAVORITE_SET = "/set/{setId}/unfavorite"
    }

    object Workout {
        const val CREATE_CUSTOM_WORKOUT = "/custom-workout"
        const val GET_WORKOUTS = "/workouts"
        const val GET_WORKOUT_DETAILS = "/workout/{workoutId}"
        const val FAVORITE_WORKOUT = "/workout/{workoutId}/favorite"
        const val UNFAVORITE_WORKOUT = "/workout/{workoutId}/unfavorite"
    }

    object Session {
        const val CREATE_SESSION = "/session"
        const val GET_SESSIONS = "/sessions"
        const val GET_TRAINEE_SESSIONS = "/trainee/{traineeId}/sessions"
        const val GET_SESSION_DETAILS = "/session/{sessionId}"
        const val EDIT_SESSION = "/session/{sessionId}/edit"
        const val CANCEL_SESSION = "/session/{sessionId}/cancel"

        const val CREATE_SESSION_FEEDBACK = "/session/{sessionId}/feedback"
        const val EDIT_SESSION_FEEDBACK = "/session/{sessionId}/edit-feedback/{feedbackId}"
        const val CREATE_SESSION_SET_FEEDBACK = "/session/{sessionId}/set/{setOrderId}/{setId}/feedback"
        const val EDIT_SESSION_SET_FEEDBACK = "/session/{sessionId}/set/{setOrderId}/{setId}/edit-feedback/{feedbackId}"
        const val GET_SET_SESSION_FEEDBACKS = "/session/{sessionId}/sets/feedback"
    }
}
