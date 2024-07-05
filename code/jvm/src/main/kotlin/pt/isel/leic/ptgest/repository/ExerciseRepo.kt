package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.exercise.model.TrainerExercise
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
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

    fun getCompanyTrainerExercises(
        companyId: UUID,
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerExercise>

    fun getTotalCompanyTrainerExercises(
        companyId: UUID,
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean
    ): Int

    fun getCompanyExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails?

    fun getTrainerExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerExercise>

    fun getTotalTrainerExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean
    ): Int

    fun favoriteExercise(trainerId: UUID, exerciseId: Int)

    fun unfavoriteExercise(trainerId: UUID, exerciseId: Int)

    fun isFavoriteExercise(trainerId: UUID, exerciseId: Int): Boolean

    fun getTrainerExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?
}
