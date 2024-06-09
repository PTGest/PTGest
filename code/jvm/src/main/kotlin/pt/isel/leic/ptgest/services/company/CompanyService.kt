package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.*

@Service
class CompanyService(
    private val transactionManager: TransactionManager
) {
    fun getCompanyTrainers(
        companyId: UUID,
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): Pair<List<Trainer>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val totalResults = companyRepo.getTotalCompanyTrainers(companyId, gender, name, excludeTraineeTrainer)
            val trainers = companyRepo.getCompanyTrainers(
                companyId,
                skip ?: 0,
                limit,
                gender,
                availability,
                name,
                excludeTraineeTrainer
            )

            return@run Pair(trainers, totalResults)
        }
    }

    fun getCompanyTrainees(
        companyId: UUID,
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): Pair<List<Trainee>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val totalResults = companyRepo.getTotalCompanyTrainees(companyId, gender, name)
            val trainees = companyRepo.getCompanyTrainees(companyId, skip ?: 0, limit, gender, name)

            return@run Pair(trainees, totalResults)
        }
    }

    //  TODO: check if the trainee already exists or is from the same company
    fun assignTrainerToTrainee(
        companyId: UUID,
        trainerId: UUID,
        traineeId: UUID
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo

            val trainer = companyRepo.getCompanyTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            if (trainer.assignedTrainees >= trainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            companyRepo.assignTrainerToTrainee(trainerId, traineeId)
        }
    }

    //  TODO: check if the trainee already exists or is from the same company
    fun reassignTrainer(
        companyId: UUID,
        trainerId: UUID,
        traineeId: UUID
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo
            val traineeRepo = it.traineeRepo

            val newTrainer = companyRepo.getCompanyTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            if (newTrainer.assignedTrainees >= newTrainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            val trainerAssigned = traineeRepo.getTrainerAssigned(traineeId)

            if (newTrainer.id == trainerAssigned) {
                throw CompanyError.TrainerAlreadyAssociatedToTrainee
            }

            companyRepo.reassignTrainer(trainerId, traineeId)
        }
    }

    fun updateTrainerCapacity(
        companyId: UUID,
        trainerId: UUID,
        capacity: Int
    ) {
        require(capacity >= 0) { "Capacity must be a positive number." }

        transactionManager.run {
            val companyRepo = it.companyRepo

            companyRepo.getCompanyTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            companyRepo.updateTrainerCapacity(companyId, trainerId, capacity)
        }
    }

    fun createCustomExercise(
        companyId: UUID,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(
                ref,
                "Reference must be a valid YouTube URL."
            ) { Validators.isYoutubeUrl(it as String) }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo
            val exerciseRepo = it.exerciseRepo

            val exerciseId = exerciseRepo.createExercise(
                name,
                description,
                muscleGroup,
                modality,
                ref
            )

            companyRepo.associateCompanyToExercise(companyId, exerciseId)

            return@run exerciseId
        }
    }

    fun getExercises(
        companyId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Pair<List<Exercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            val exercises = exerciseRepo.getCompanyExercises(
                companyId,
                skip ?: 0,
                limit,
                name,
                muscleGroup,
                modality
            )

            val totalExercises = exerciseRepo.getTotalCompanyExercises(
                companyId,
                name,
                muscleGroup,
                modality
            )

            return@run Pair(exercises, totalExercises)
        }
    }

    fun getExerciseDetails(
        companyId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            return@run exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId)
                ?: throw CompanyError.ExerciseNotFoundError
        }
}
