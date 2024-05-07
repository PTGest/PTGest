package pt.isel.leic.ptgest.services.workout

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class WorkoutError : BaseError() {

    data object ExerciseNotFoundError : WorkoutError() {
        private fun readResolve(): Any = ExerciseNotFoundError
        override val message: String get() = "Exercise not found."
    }

    data object SetNotFoundError : WorkoutError() {
        private fun readResolve(): Any = SetNotFoundError
        override val message: String get() = "Set not found."
    }

    data object InvalidSetTypeError : WorkoutError() {
        private fun readResolve(): Any = InvalidSetTypeError
        override val message: String get() = "Invalid set type."
    }
}
