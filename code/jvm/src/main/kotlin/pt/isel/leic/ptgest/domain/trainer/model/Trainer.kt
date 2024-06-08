package pt.isel.leic.ptgest.domain.trainer.model

import pt.isel.leic.ptgest.domain.user.Gender
import java.util.UUID

data class Trainer(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val assignedTrainees: Int,
    val capacity: Int
)
