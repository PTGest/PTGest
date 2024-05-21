package pt.isel.leic.ptgest.http.controllers.auth.model.request

import jakarta.validation.constraints.Pattern

data class ForgetPasswordRequest(
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$",
        message = "Invalid email address."
    )
    val email: String
) {
    init {
        email.trim()
    }
}
