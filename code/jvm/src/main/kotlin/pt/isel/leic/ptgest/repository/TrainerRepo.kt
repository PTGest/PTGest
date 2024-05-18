package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Workout
import java.util.UUID

interface TrainerRepo {

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID

    fun getExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)

    fun getLastSetNameId(trainerId: UUID): Int

    fun getLastWorkoutNameId(trainerId: UUID): Int

    fun getSet(trainerId: UUID, setId: Int): Set?

    fun getSet(setId: Int): Set

    fun getSetExercises(setId: Int): List<SetExerciseDetails>

    fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout?

    fun getWorkoutSetIds(workoutId: Int): List<Int>
}
