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

    fun editExercise(
        exerciseId: Int,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    )

    fun deleteExercise(exerciseId: Int)

//   Set related methods

    fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int

    fun editSet(setId: Int, name: String, notes: String?, type: SetType)

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)

    fun removeExercisesFromSet(setId: Int)

    fun deleteSet(setId: Int)

//   Workout related methods

    fun createWorkout(trainerId: UUID, name: String, description: String?, muscleGroup: List<MuscleGroup>): Int

    fun editWorkout(workoutId: Int, name: String, description: String?, muscleGroup: List<MuscleGroup>)

    fun deleteWorkout(workoutId: Int)

    fun associateSetToWorkout(orderId: Int, setId: Int, workoutId: Int)

    fun removeSetsFromWorkout(workoutId: Int)
}
