package pt.isel.leic.ptgest.http.model.auth.request

import jakarta.validation.constraints.NotEmpty

data class RefreshTokenRequest(
    @field:NotEmpty(message = "Refresh token must not be empty")
    val refreshToken: String
) {
    init {
        refreshToken.trim()
    }
}
