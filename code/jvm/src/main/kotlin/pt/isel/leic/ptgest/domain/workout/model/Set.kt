package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class Set(
    val name: String,
    val notes: String?,
    val type: SetType
)
