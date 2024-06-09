package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.user.Role
import java.util.UUID

data class AuthenticationDetails(
    val id: UUID,
    val role: Role,
    val tokens: TokenPair
)
