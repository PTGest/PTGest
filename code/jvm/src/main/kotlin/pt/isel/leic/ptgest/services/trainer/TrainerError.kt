package pt.isel.leic.ptgest.services.trainer

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TrainerError : BaseError() {

    data object ExerciseNotFoundError : TrainerError() {
        private fun readResolve(): Any = ExerciseNotFoundError
        override val message: String get() = "Exercise not found."
    }
}
