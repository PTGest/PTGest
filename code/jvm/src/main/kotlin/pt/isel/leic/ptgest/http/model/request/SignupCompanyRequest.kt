package pt.isel.leic.ptgest.http.model.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupCompanyRequest (
    @NotEmpty
    val name: String,

    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    val email: String,

    @field:Size(min = 8)
    val password: String
)
