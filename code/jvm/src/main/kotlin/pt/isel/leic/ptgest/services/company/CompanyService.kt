package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup
import pt.isel.leic.ptgest.domain.company.CompanyTrainers
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class CompanyService(
    private val transactionManager: TransactionManager
) {

    fun getCompanyTrainers(
        skip: Int?,
        limit: Int?,
        companyId: UUID
    ): CompanyTrainers {
        if (limit != null) {
            require(limit > 0) { "Limit must be a positive number." }
        }
        if (skip != null) {
            require(skip >= 0) { "Skip must be a positive number." }
        }

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val totalResults = companyRepo.getTotalCompanyTrainers(companyId)
            val trainers = companyRepo.getCompanyTrainers(companyId, skip ?: 0, limit)

            return@run CompanyTrainers(trainers, totalResults)
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

            if (trainer.totalTrainees >= trainer.capacity) {
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

            if (newTrainer.totalTrainees >= newTrainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            val trainerAssigned = companyRepo.getTrainerAssigned(traineeId)

            companyRepo.getCompanyTrainer(trainerAssigned, companyId)?.totalTrainees
                ?: throw CompanyError.TrainerNotFound

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
        trainerId: UUID,
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            val exerciseId = workoutRepo.createExercise(name, description, muscleGroup, exerciseType, ref)
            trainerRepo.associateTrainerToExercise(exerciseId, trainerId)

            return@run exerciseId
        }
}
