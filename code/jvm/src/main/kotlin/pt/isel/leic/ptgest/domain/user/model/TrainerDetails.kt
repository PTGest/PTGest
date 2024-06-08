package pt.isel.leic.ptgest.domain.user.model

import pt.isel.leic.ptgest.domain.user.Gender

data class TrainerDetails(
    val gender: Gender,
    val phoneNumber: String?
)
