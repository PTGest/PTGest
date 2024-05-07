package pt.isel.leic.ptgest.domain.workout.model

data class SetExercise(
    val exerciseId: Int,
    val details: Map<String, Any>
)
