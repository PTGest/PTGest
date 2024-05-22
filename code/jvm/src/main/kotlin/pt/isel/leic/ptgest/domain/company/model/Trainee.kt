package pt.isel.leic.ptgest.domain.company.model

import pt.isel.leic.ptgest.domain.common.Gender
import java.util.UUID

data class Trainee(
    val traineeId: UUID,
    val traineeName: String,
    val gender: Gender,
    val trainerId: UUID?,
    val trainerName: String?,
)
