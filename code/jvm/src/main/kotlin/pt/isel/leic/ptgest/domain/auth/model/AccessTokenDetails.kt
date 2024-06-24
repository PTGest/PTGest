package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.user.Role
import java.util.Date
import java.util.UUID

data class AccessTokenDetails(
    val userId: UUID,
    val role: Role,
    val expirationDate: Date,
    val version: Int
)
