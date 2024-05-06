package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.company.Trainer
import pt.isel.leic.ptgest.domain.workout.ExerciseDetails
import java.util.*

interface CompanyRepo {

    fun getCompanyTrainers(userId: UUID, skip: Int, limit: Int?): List<Trainer>

    fun getTotalCompanyTrainers(userId: UUID): Int

    fun getCompanyTrainer(trainerId: UUID, companyId: UUID): Trainer?

    fun getTrainerAssigned(traineeId: UUID): UUID

    fun assignTrainerToTrainee(trainerId: UUID, traineeId: UUID)

    fun reassignTrainer(trainerId: UUID, traineeId: UUID)

    fun updateTrainerCapacity(companyId: UUID, trainerId: UUID, capacity: Int)

    fun associateCompanyToExercise(companyId: UUID, exerciseId: Int)

    fun getExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails?
}
