package pt.isel.leic.ptgest.services.errors

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

    data object TrainerAlreadyAssociatedToTrainee : CompanyError() {
        private fun readResolve(): Any = TrainerAlreadyAssociatedToTrainee
        override val message: String get() = "Trainer is already associated to the trainee."
    }

    data object TraineeNotFromCompany : CompanyError() {
        private fun readResolve(): Any = TraineeNotFromCompany
        override val message: String get() = "Trainee is not from the company."
    }
}
