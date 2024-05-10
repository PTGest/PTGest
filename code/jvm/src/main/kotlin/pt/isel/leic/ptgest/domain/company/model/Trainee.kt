package pt.isel.leic.ptgest.domain.company.model

import pt.isel.leic.ptgest.domain.common.Gender
import java.util.UUID

data class Trainee(
    val id: UUID,
    val name: String,
    val gender: Gender
)
