package pt.isel.leic.ptgest.services.trainer

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercises
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
