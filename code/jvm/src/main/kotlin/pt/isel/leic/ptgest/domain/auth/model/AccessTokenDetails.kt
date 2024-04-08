package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

data class AccessTokenDetails(
    val userId: UUID,
    val role: Role,
    val expirationDate: Date
)
