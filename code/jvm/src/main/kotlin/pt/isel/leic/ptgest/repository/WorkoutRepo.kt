package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.domain.workout.ExerciseDetails

interface WorkoutRepo {

    fun createExercise(name: String, description: String?, category: ExerciseType, ref: String?): Int

    fun getExerciseDetails(exerciseId: Int): ExerciseDetails?

    fun createSet(name: String, notes: String?, type: SetType, details: String): Int

    fun associateExerciseToSet(exerciseId: Int, setId: Int)
}
