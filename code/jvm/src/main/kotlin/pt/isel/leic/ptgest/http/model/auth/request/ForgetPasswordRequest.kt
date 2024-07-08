package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class ForgetPasswordRequest(
    @field:NotEmpty(message = "Email cannot be empty.")
    @field:Email(message = "Invalid email address.")
    val email: String
) {
    init {
        email.trim()
    }
}
