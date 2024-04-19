package pt.isel.leic.ptgest.domain.user

import pt.isel.leic.ptgest.domain.common.Gender

data class TrainerDetails(
    val name: String,
    val email: String,
    val gender: Gender,
    val phoneNumber: String?
)
