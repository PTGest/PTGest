package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class ExerciseDetails(
    val name: String,
    val description: String?,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val ref: String?
)
