package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import java.util.*

interface ExerciseRepo {

    fun createExercise(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int

    fun getCompanyExercises(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalCompanyExercises(
        companyId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getCompanyExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails?

    fun getTrainerExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalTrainerExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun favoriteExercise(trainerId: UUID, exerciseId: Int)

    fun unfavoriteExercise(trainerId: UUID, exerciseId: Int)

    fun getFavoriteExercises(trainerId: UUID): List<Int>

    fun getFavoriteExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalFavoriteExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getTrainerExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?
}
