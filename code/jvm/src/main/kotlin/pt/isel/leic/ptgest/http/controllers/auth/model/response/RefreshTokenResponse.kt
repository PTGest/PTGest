package pt.isel.leic.ptgest.http.controllers.auth.model.response

import pt.isel.leic.ptgest.domain.auth.model.Token

data class RefreshTokenResponse(
    val accessToken: Token,
    val refreshToken: Token
)