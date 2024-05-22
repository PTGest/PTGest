package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.company.model.CompanyTrainees
import pt.isel.leic.ptgest.domain.company.model.CompanyTrainers
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercises
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
    ): CompanyTrainers {
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

            return@run CompanyTrainers(trainers, totalResults)
        }
    }

    fun getCompanyTrainees(
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        name: String?,
        companyId: UUID
    ): CompanyTrainees {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val totalResults = companyRepo.getTotalCompanyTrainees(companyId, gender, name)
            val trainees = companyRepo.getCompanyTrainees(companyId, skip ?: 0, limit, gender, name)

            return@run CompanyTrainees(trainees, totalResults)
        }
    }

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

    fun reassignTrainer(
        trainerId: UUID,
        traineeId: UUID,
        companyId: UUID
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo

            val newTrainer = companyRepo.getCompanyTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            if (newTrainer.assignedTrainees >= newTrainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            val trainerAssigned = companyRepo.getTrainerAssigned(traineeId)

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

    fun associateCompanyToExercise(
        companyId: UUID,
        exerciseId: Int
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo
            companyRepo.associateCompanyToExercise(companyId, exerciseId)
        }
    }

    fun getExercises(
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        trainerId: UUID
    ): Exercises {
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

            return@run Exercises(exercises, totalExercises)
        }
    }
}
