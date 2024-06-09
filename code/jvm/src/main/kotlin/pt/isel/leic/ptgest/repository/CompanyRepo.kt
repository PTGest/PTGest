package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.user.Gender
import java.util.UUID

interface CompanyRepo {

    fun getTrainers(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): List<Trainer>

    fun getTotalTrainers(
        companyId: UUID,
        gender: Gender?,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): Int

    fun getTrainees(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee>

    fun getTotalTrainees(companyId: UUID, gender: Gender?, name: String?): Int

    fun getTrainer(trainerId: UUID, companyId: UUID): Trainer?

    fun assignTrainerToTrainee(trainerId: UUID, traineeId: UUID)

    fun reassignTrainer(trainerId: UUID, traineeId: UUID)

    fun updateTrainerCapacity(companyId: UUID, trainerId: UUID, capacity: Int)

    fun associateCompanyToExercise(companyId: UUID, exerciseId: Int)
}
