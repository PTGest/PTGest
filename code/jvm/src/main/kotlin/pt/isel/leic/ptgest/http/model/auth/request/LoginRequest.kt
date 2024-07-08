package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotEmpty(message = "Email is required.")
    @field:Email(message = "Invalid email.")
    val email: String,

    @field:Size(min = 8, message = "Password must have at least 8 characters.")
    val password: String
) {
    init {
        email.trim()
        password.trim()
    }
}
