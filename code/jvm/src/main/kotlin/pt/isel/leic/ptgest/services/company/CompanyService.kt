package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.company.model.CompanyTrainers
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

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

    fun associateCompanyToExercise(
        companyId: UUID,
        exerciseId: Int
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo
            companyRepo.associateCompanyToExercise(companyId, exerciseId)
        }
    }
}
