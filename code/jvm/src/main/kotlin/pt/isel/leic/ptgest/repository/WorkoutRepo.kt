package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup
import pt.isel.leic.ptgest.domain.common.SetType

interface WorkoutRepo {

    fun createExercise(
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int

    fun createSet(name: String, notes: String?, type: SetType): Int

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)
}
