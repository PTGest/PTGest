package pt.isel.leic.ptgest.services.company

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class CompanyError : BaseError() {

    data object TrainerCapacityReached : CompanyError() {
        private fun readResolve(): Any = TrainerCapacityReached
        override val message: String get() = "Trainer capacity reached."
    }

    data object TrainerNotFound : CompanyError() {
        private fun readResolve(): Any = TrainerNotFound
        override val message: String get() = "Trainer not found or does not belong to the company."
    }
}
