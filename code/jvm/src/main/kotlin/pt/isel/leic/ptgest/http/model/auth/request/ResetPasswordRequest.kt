package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.Size

data class ResetPasswordRequest(
    @field:Size(min = 8, message = "Password must have at least 8 characters")
    val password: String
) {
    init {
        password.trim()
    }
}
