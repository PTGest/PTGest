package pt.isel.leic.ptgest.domain.exercise.model

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class TrainerExercise(
    val id: Int,
    val name: String,
    val muscleGroup: List<MuscleGroup>,
    val modality: Modality,
    val isFavorite: Boolean
)
