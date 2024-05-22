package pt.isel.leic.ptgest.services.trainer

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TrainerError : BaseError() {
    data object TrainerNotAssigned : TrainerError() {
        private fun readResolve(): Any = TrainerNotAssigned
        override val message: String get() = "Trainer not assigned to any company."
    }
}
