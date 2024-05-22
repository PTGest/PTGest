package pt.isel.leic.ptgest.services.trainee

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.workout.WorkoutError
import java.util.UUID

@Service
class TraineeService(
    private val transactionManager: TransactionManager
) {

    fun getExerciseDetails(
        exerciseId: Int,
        userId: UUID
    ): ExerciseDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val companyRepo = it.companyRepo
            val traineeRepo = it.traineeRepo

            val trainerId = traineeRepo.getTrainerAssigned(userId)
                ?: throw TraineeError.TraineeNotAssigned
            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            trainerRepo.getExerciseDetails(userId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw WorkoutError.ExerciseNotFoundError
        }
}
