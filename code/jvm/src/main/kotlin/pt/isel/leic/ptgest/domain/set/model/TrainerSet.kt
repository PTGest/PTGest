package pt.isel.leic.ptgest.domain.set.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class TrainerSet(
    val id: Int,
    val name: String,
    val notes: String?,
    val type: SetType,
    val isFavorite: Boolean
)
