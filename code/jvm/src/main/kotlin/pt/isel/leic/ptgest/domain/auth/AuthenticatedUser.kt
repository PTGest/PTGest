package pt.isel.leic.ptgest.domain.auth

//check token expiration
data class AuthenticatedUser(
    val id: String,
    val token: String,
    val role: String
)
