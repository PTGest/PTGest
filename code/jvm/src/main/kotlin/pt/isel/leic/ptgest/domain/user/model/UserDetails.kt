package pt.isel.leic.ptgest.domain.user.model

import pt.isel.leic.ptgest.domain.user.Role
import java.util.UUID

data class UserDetails(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val active: Boolean
)
