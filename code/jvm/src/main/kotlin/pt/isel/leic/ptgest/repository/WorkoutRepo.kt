package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import java.util.UUID

interface WorkoutRepo {

//  Exercise related methods

    fun createExercise(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int

    fun deleteExercise(exerciseId: Int)

//   Set related methods

    fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)

    fun deleteSet(setId: Int)

//   Workout related methods

    fun createWorkout(trainerId: UUID, name: String, description: String?, muscleGroup: List<MuscleGroup>): Int

    fun deleteWorkout(workoutId: Int)

    fun associateSetToWorkout(orderId: Int, setId: Int, workoutId: Int)
}
