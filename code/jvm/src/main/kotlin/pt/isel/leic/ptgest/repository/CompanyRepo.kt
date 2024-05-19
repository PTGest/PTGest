package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.company.model.Trainee
import pt.isel.leic.ptgest.domain.company.model.Trainer
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import java.util.UUID

interface CompanyRepo {

    fun getCompanyTrainers(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): List<Trainer>

    fun getTotalCompanyTrainers(
        companyId: UUID,
        gender: Gender?,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): Int

    fun getCompanyTrainees(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee>

    fun getTotalCompanyTrainees(companyId: UUID, gender: Gender?, name: String?): Int

    fun getCompanyTrainer(trainerId: UUID, companyId: UUID): Trainer?

    fun getTrainerAssigned(traineeId: UUID): UUID

    fun assignTrainerToTrainee(trainerId: UUID, traineeId: UUID)

    fun reassignTrainer(trainerId: UUID, traineeId: UUID)

    fun updateTrainerCapacity(companyId: UUID, trainerId: UUID, capacity: Int)

    fun associateCompanyToExercise(companyId: UUID, exerciseId: Int)

    fun getExercises(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalExercises(
        companyId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails?
}
