package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

//check token expiration
data class AuthenticatedUser(
    val id: UUID,
    val role: Role
)
