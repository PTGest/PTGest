package pt.isel.leic.ptgest.domain.user

import pt.isel.leic.ptgest.domain.common.Gender
import java.util.*

data class TraineeDetails(
    val name: String,
    val email: String,
    val gender: Gender,
    val birthdate: Date,
    val phoneNumber: String?
)
