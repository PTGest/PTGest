package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.TrainerWorkout
import pt.isel.leic.ptgest.domain.workout.model.Workout
import java.util.*

interface WorkoutRepo {

    fun createWorkout(name: String, description: String?, muscleGroup: List<MuscleGroup>): Int

    fun getWorkoutBySets(sets: List<Int>): Int?

    fun associateSetToWorkout(orderId: Int, setId: Int, workoutId: Int)

    fun getLastWorkoutNameId(trainerId: UUID): Int

    fun getWorkouts(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerWorkout>

    fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?, isFavorite: Boolean): Int

    fun isWorkoutFavorite(trainerId: UUID, workoutId: Int): Boolean

    fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout?

    fun getWorkoutDetails(workoutId: Int): Workout?

    fun getWorkoutSetIds(workoutId: Int): List<Int>

    fun favoriteWorkout(trainerId: UUID, workoutId: Int)

    fun unfavoriteWorkout(trainerId: UUID, workoutId: Int)

    fun isWorkoutOwner(trainerId: UUID, workoutId: Int): Boolean
}
