package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.common.Role
import java.util.UUID

data class AuthenticationDetails(
    val userId: UUID,
    val role: Role,
    val tokens: TokenPair
)
