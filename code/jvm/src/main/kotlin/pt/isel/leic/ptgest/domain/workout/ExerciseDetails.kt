package pt.isel.leic.ptgest.domain.workout

import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup

data class ExerciseDetails(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val ref: String?
)
