package pt.isel.leic.ptgest.domain.company.model

import java.util.UUID
import pt.isel.leic.ptgest.domain.common.Gender

data class Trainer(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val totalTrainees: Int,
    val capacity: Int
)
