package pt.isel.leic.ptgest.domain.set.model

import pt.isel.leic.ptgest.domain.set.ExerciseDetailsType
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class SetExerciseDetails(
    val id: Int,
    val name: String,
    val muscleGroup: List<MuscleGroup>,
    val modality: Modality,
    val details: Map<ExerciseDetailsType, Number>
)
