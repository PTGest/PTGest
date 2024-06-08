package pt.isel.leic.ptgest.http.model.auth.response

import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.TokenPair

data class RefreshTokenResponse(
    val accessToken: Token,
    val refreshToken: Token
) {
    constructor(tokens: TokenPair) : this(tokens.accessToken, tokens.refreshToken)
}
