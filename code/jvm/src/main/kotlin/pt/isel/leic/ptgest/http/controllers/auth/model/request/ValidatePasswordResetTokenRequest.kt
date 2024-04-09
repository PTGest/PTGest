package pt.isel.leic.ptgest.http.controllers.auth.model.request

data class ValidatePasswordResetTokenRequest(
    val token: String
)
