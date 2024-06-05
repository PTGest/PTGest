package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.company.model.Trainee
import pt.isel.leic.ptgest.domain.company.model.Trainer
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.UUID

@Service
class CompanyService(
    private val transactionManager: TransactionManager
) {

    fun getCompanyTrainers(
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?,
        companyId: UUID
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
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        name: String?,
        companyId: UUID
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
        trainerId: UUID,
        traineeId: UUID,
        companyId: UUID
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
        trainerId: UUID,
        traineeId: UUID,
        companyId: UUID
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
        trainerId: UUID,
        companyId: UUID,
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
            Validators.ValidationRequest(ref, "Reference must be a valid YouTube URL.") { Validators.isYoutubeUrl(it as String) }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo
            val workoutRepo = it.workoutRepo

            val exerciseId = workoutRepo.createExercise(
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
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        trainerId: UUID
    ): Pair<List<Exercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val exercises = companyRepo.getExercises(
                trainerId,
                skip ?: 0,
                limit,
                name,
                muscleGroup,
                modality
            )

            val totalExercises = companyRepo.getTotalExercises(
                trainerId,
                name,
                muscleGroup,
                modality
            )

            return@run Pair(exercises, totalExercises)
        }
    }

    fun getExerciseDetails(
        exerciseId: Int,
        userId: UUID
    ): ExerciseDetails =
        transactionManager.run {
            val companyRepo = it.companyRepo

            return@run companyRepo.getExerciseDetails(userId, exerciseId)
                ?: throw CompanyError.ExerciseNotFoundError
        }

    fun editExercise(
        companyId: UUID,
        exerciseId: Int,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ) {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(ref, "Reference must be a valid YouTube URL.") { Validators.isYoutubeUrl(it as String) }
        )

        transactionManager.run {
            val companyRepo = it.companyRepo
            val workoutRepo = it.workoutRepo

            companyRepo.getExerciseDetails(companyId, exerciseId)
                ?: throw CompanyError.ExerciseNotFoundError

            workoutRepo.editExercise(
                exerciseId,
                name,
                description,
                muscleGroup,
                modality,
                ref
            )
        }
    }

    fun deleteExercise(
        companyId: UUID,
        exerciseId: Int
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo
            val workoutRepo = it.workoutRepo

            companyRepo.getExerciseDetails(companyId, exerciseId)
                ?: throw CompanyError.ExerciseNotFoundError

            workoutRepo.deleteExercise(exerciseId)
        }
    }
}
