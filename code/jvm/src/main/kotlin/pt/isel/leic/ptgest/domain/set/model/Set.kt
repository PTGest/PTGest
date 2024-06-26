package pt.isel.leic.ptgest.domain.set.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class Set(
    val id: Int,
    val name: String,
    val notes: String?,
    val type: SetType
)
