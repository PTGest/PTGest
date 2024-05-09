package pt.isel.leic.ptgest.domain.company.model

import pt.isel.leic.ptgest.domain.common.Gender
import java.util.UUID

data class Trainer(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val totalTrainees: Int,
    val capacity: Int
)
