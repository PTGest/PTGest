package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import java.util.UUID

interface WorkoutRepo {

    fun createExercise(
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int

    fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)

    fun createWorkout(trainerId: UUID, name: String, description: String?, category: MuscleGroup): Int

    fun associateSetToWorkout(orderId: Int, setId: Int, workoutId: Int)
}
