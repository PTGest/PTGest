package pt.isel.leic.ptgest.services.trainer

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TrainerError : BaseError() {

    data object TraineeNotAssignedToTrainerError : TrainerError() {
        private fun readResolve(): Any = TraineeNotAssignedToTrainerError
        override val message: String get() = "Trainee not assigned to the trainer."
    }

    data object ResourceNotFoundError : TrainerError() {
        private fun readResolve(): Any = ResourceNotFoundError
        override val message: String get() = "Resource not found."
    }

    data object ResourceAlreadyFavoriteError : TrainerError() {
        private fun readResolve(): Any = ResourceAlreadyFavoriteError
        override val message: String get() = "Resource already favorite."
    }

    data object ResourceNotFavoriteError : TrainerError() {
        private fun readResolve(): Any = ResourceNotFavoriteError
        override val message: String get() = "Resource not favorite."
    }
}
