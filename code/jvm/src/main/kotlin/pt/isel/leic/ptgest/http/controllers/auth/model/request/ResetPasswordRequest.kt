package pt.isel.leic.ptgest.http.controllers.auth.model.request

data class ResetPasswordRequest(
    val token: String,
    val password: String
)
