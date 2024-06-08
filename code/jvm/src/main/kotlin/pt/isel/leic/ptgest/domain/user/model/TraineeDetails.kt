package pt.isel.leic.ptgest.domain.user.model

import pt.isel.leic.ptgest.domain.user.Gender
import java.util.Date

data class TraineeDetails(
    val gender: Gender,
    val birthdate: Date,
    val phoneNumber: String?
)
