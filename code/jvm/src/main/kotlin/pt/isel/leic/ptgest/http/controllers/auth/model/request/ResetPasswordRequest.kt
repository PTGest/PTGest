package pt.isel.leic.ptgest.http.controllers.auth.model.request

import jakarta.validation.constraints.Size

data class ResetPasswordRequest(
    @field:Size(min = 8)
    val password: String
)
