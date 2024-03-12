package pt.isel.leic.ptgest.http.model.request

data class LoginRequest (
    val email: String,
    val password: String
)
