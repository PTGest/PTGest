package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class WorkoutSet(
    val id: Int,
    val orderId: Int,
    val name: String,
    val notes: String?,
    val type: SetType
)
