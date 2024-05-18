package pt.isel.leic.ptgest.services.trainer

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Exercises
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.utils.Validators
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

    fun getExercises(
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        trainerId: UUID,
        userRole: Role
    ): Exercises {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            return@run if (userRole == Role.HIRED_TRAINER) {
                it.getHiredTrainerExercises(
                    trainerId,
                    skip,
                    limit,
                    name,
                    muscleGroup,
                    modality
                )
            } else {
                it.getIndependentTrainerExercises(
                    trainerId,
                    skip,
                    limit,
                    name,
                    muscleGroup,
                    modality
                )
            }
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

    private fun Transaction.getHiredTrainerExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Exercises {
        val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

        val companyExercises = companyRepo.getExercises(
            companyId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalCompanyExercises = companyRepo.getTotalExercises(
            companyId,
            name,
            muscleGroup,
            modality
        )

        val trainerExercises = trainerRepo.getExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalTrainerExercises = trainerRepo.getTotalExercises(
            trainerId,
            name,
            muscleGroup,
            modality
        )

        return Exercises(
            companyExercises + trainerExercises,
            totalCompanyExercises + totalTrainerExercises
        )
    }

    private fun Transaction.getIndependentTrainerExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Exercises {
        val exercises = trainerRepo.getExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )

        val totalExercises = trainerRepo.getTotalExercises(
            trainerId,
            name,
            muscleGroup,
            modality
        )

        return Exercises(exercises, totalExercises)
    }
}
