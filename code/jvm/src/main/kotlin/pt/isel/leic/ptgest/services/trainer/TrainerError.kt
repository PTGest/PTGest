package pt.isel.leic.ptgest.services.trainer

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TrainerError : BaseError() {
    data object ExerciseNotFoundError : TrainerError() {
        private fun readResolve(): Any = ExerciseNotFoundError
        override val message: String get() = "Exercise not found."
    }

    data object SetNotFoundError : TrainerError() {
        private fun readResolve(): Any = SetNotFoundError
        override val message: String get() = "Set not found."
    }

    data object WorkoutNotFoundError : TrainerError() {
        private fun readResolve(): Any = WorkoutNotFoundError
        override val message: String get() = "Workout not found or does not belong to the trainer."
    }

    data object TraineeNotAssignedToTrainerError : TrainerError() {
        private fun readResolve(): Any = TraineeNotAssignedToTrainerError
        override val message: String get() = "Trainee not assigned to the trainer."
    }
}
