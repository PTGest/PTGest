package pt.isel.leic.ptgest.services.trainee

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

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
            val traineeRepo = it.traineeRepo
            val exerciseRepo = it.exerciseRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError
            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            exerciseRepo.getTrainerExerciseDetails(traineeId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw TraineeError.ExerciseNotFoundError
        }

    fun getSetDetails(
        traineeId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            val setRepo = it.setRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            val set = setRepo.getSet(trainerId, setId)
                ?: throw TraineeError.SetNotFoundError
            val exercises = setRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun getWorkoutDetails(
        traineeId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            val workout = workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TraineeError.WorkoutNotFoundError

            val sets = workoutRepo.getWorkoutSetIds(workoutId).mapNotNull { setId ->
                val set = setRepo.getSet(trainerId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }
}
