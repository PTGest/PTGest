package pt.isel.leic.ptgest.services.trainee

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TraineeError : BaseError() {

    data object TraineeNotAssigned : TraineeError() {
        private fun readResolve(): Any = TraineeNotAssigned
        override val message: String get() = "Trainee not assigned to any trainer."
    }
}
