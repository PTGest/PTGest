package pt.isel.leic.ptgest.domain.common

data class SetDetails(
    val exerciseId: Int,
    val exerciseType: ExerciseType,
    val details: Map<String, Any>
)
