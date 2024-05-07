package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class Workout(
    val name: String,
    val description: String?,
    val type: MuscleGroup
)
