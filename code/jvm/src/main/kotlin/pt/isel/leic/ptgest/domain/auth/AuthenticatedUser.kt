package pt.isel.leic.ptgest.domain.auth

import pt.isel.leic.ptgest.domain.common.Role

//check token expiration
data class AuthenticatedUser(
    val id: String,
    val token: String,
    val role: Role
)
