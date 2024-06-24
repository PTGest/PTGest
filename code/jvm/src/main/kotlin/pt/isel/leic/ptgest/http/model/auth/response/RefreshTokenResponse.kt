package pt.isel.leic.ptgest.http.model.auth.response

import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import java.util.Date

data class RefreshTokenResponse(
    val accessTokenExpiration: Date,
    val refreshTokenExpiration: Date
) {
    constructor(tokens: TokenPair) : this(tokens.accessToken.expirationDate, tokens.refreshToken.expirationDate)
}
