package pt.isel.leic.ptgest.domain.user.model

import pt.isel.leic.ptgest.domain.common.Gender
import java.util.Date

data class TraineeDetails(
    val name: String,
    val email: String,
    val gender: Gender,
    val birthdate: Date,
    val phoneNumber: String?
)
