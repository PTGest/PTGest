package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class TrainerWorkout(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val isFavorite: Boolean
)
