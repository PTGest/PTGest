package pt.isel.leic.ptgest.http.controllers.auth.model.response

import pt.isel.leic.ptgest.domain.auth.model.Token

internal data class LoginResponse(
    val accessToken: Token,
    val refreshToken: Token
)
