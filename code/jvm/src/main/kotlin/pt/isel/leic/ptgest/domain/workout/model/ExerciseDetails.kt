package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class ExerciseDetails(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val modality: Modality,
    val ref: String?
)
