package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TrainerError : BaseError() {

    data object TrainerNotAssignedToTraineeError : TrainerError() {
        private fun readResolve(): Any = TrainerNotAssignedToTraineeError
        override val message: String get() = "Trainer not assigned to trainee."
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
