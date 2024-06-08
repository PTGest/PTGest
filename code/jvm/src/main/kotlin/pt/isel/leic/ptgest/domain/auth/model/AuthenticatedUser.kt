package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.user.Role
import java.util.UUID

data class AuthenticatedUser(
    val id: UUID,
    val role: Role
)
