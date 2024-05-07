package pt.isel.leic.ptgest.services.trainer

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {
    fun associateTrainerToExercise(
        trainerId: UUID,
        exerciseId: Int
    ) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            trainerRepo.associateTrainerToExercise(trainerId, exerciseId)
        }
    }

    fun getExerciseDetails(
        trainerId: UUID,
        userRole: Role,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val companyRepo = it.companyRepo
            return@run trainerRepo.getExerciseDetails(trainerId, exerciseId) ?: if (userRole == Role.HIRED_TRAINER) {
                val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)
                companyRepo.getExerciseDetails(companyId, exerciseId) ?: throw TrainerError.ExerciseNotFoundError
            } else {
                throw TrainerError.ExerciseNotFoundError
            }
        }

    fun getSetDetails(
        trainerId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val set = trainerRepo.getSet(trainerId, setId) ?: throw TrainerError.SetNotFoundError
            val exercises = trainerRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun getWorkoutDetails(
        trainerId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val workout = trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.WorkoutNotFoundError

            val sets = trainerRepo.getWorkoutSetIds(workoutId).map { setId ->
                val set = trainerRepo.getSet(setId)
                val exercises = trainerRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }
}
