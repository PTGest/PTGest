package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.company.model.Trainer
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import java.util.UUID

interface CompanyRepo {

    fun getCompanyTrainers(companyId: UUID, skip: Int, limit: Int?, gender: Gender?, availability: Order): List<Trainer>

    fun getTotalCompanyTrainers(companyId: UUID, gender: Gender?): Int

    fun getCompanyTrainer(trainerId: UUID, companyId: UUID): Trainer?

    fun getTrainerAssigned(traineeId: UUID): UUID

    fun assignTrainerToTrainee(trainerId: UUID, traineeId: UUID)

    fun reassignTrainer(trainerId: UUID, traineeId: UUID)

    fun updateTrainerCapacity(companyId: UUID, trainerId: UUID, capacity: Int)

    fun associateCompanyToExercise(companyId: UUID, exerciseId: Int)

    fun getExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails?
}
