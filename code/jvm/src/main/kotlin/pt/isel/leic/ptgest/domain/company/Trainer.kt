package pt.isel.leic.ptgest.domain.company

import pt.isel.leic.ptgest.domain.common.Gender

data class Trainer(
    val id: Int,
    val name: String,
    val gender: Gender,
    val totalTrainees: Int,
    val capacity: Int
)
