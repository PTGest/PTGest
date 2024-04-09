package pt.isel.leic.ptgest.http.controllers.auth.model.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class ForgetRequest(
    @NotEmpty
    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    val email: String
)
