package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.common.Role
import java.time.LocalDate
import java.util.*

data class TokenDetails(
    val tokenHash: String,
    val userId: UUID,
    val role: Role,
    val creationDate: LocalDate,
    val expirationDate: LocalDate
)