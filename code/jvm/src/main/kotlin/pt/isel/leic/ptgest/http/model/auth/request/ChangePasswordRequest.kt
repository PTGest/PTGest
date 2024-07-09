package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:Size(min = 8, message = "Password must have at least 8 characters")
    val currentPassword: String,
    @field:Size(min = 8, message = "Password must have at least 8 characters")
    val newPassword: String
) {
    init {
        currentPassword.trim()
        newPassword.trim()
    }
}
