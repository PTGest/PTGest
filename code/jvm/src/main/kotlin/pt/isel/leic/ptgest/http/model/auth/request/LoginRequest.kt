package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$",
        message = "Invalid email address."
    )
    val email: String,

    @field:Size(min = 8, message = "Password must have at least 8 characters.")
    val password: String
) {
    init {
        email.trim()
        password.trim()
    }
}
