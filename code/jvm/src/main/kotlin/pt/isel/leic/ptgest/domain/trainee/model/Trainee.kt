package pt.isel.leic.ptgest.domain.trainee.model

import pt.isel.leic.ptgest.domain.user.Gender
import java.util.*

data class Trainee(
    val traineeId: UUID,
    val traineeName: String,
    val gender: Gender,
    val trainerId: UUID?,
    val trainerName: String?
)
