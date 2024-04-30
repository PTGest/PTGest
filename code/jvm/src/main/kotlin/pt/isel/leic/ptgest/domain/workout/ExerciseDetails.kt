package pt.isel.leic.ptgest.domain.workout

import pt.isel.leic.ptgest.domain.common.ExerciseType

data class ExerciseDetails(
    val id: Int,
    val name: String,
    val description: String?,
    val category: ExerciseType,
    val ref: String?
)
