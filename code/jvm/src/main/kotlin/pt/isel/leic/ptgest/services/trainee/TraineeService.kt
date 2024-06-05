package pt.isel.leic.ptgest.services.trainee

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class TraineeService(
    private val transactionManager: TransactionManager
) {

    fun getExerciseDetails(
        traineeId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val companyRepo = it.companyRepo
            val traineeRepo = it.traineeRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned
            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            trainerRepo.getExerciseDetails(traineeId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw TraineeError.ExerciseNotFoundError
        }

    fun getSetDetails(
        traineeId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val traineeRepo = it.traineeRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            val set = trainerRepo.getSet(trainerId, setId)
                ?: throw TraineeError.SetNotFoundError
            val exercises = trainerRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun getWorkoutDetails(
        traineeId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val traineeRepo = it.traineeRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            val workout = trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TraineeError.WorkoutNotFoundError

            val sets = trainerRepo.getWorkoutSetIds(workoutId).map { setId ->
                val set = trainerRepo.getSet(setId)
                val exercises = trainerRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }
}
