package pt.isel.leic.ptgest.services.trainee

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class TraineeError : BaseError() {

    data object TraineeNotAssignedError : TraineeError() {
        private fun readResolve(): Any = TraineeNotAssignedError
        override val message: String get() = "Trainee not assigned to any trainer."
    }

    data object TraineeNotFoundError : TraineeError() {
        private fun readResolve(): Any = TraineeNotFoundError
        override val message: String get() = "Trainee not found."
    }

    data object ExerciseNotFoundError : TraineeError() {
        private fun readResolve(): Any = ExerciseNotFoundError
        override val message: String get() = "Exercise not found."
    }

    data object SetNotFoundError : TraineeError() {
        private fun readResolve(): Any = SetNotFoundError
        override val message: String get() = "Set not found."
    }

    data object WorkoutNotFoundError : TraineeError() {
        private fun readResolve(): Any = WorkoutNotFoundError
        override val message: String get() = "Workout not found or does not belong to the trainer."
    }
}
