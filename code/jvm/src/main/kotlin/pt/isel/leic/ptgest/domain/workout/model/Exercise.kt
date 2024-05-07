package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val details: Map<String, Any>
)
