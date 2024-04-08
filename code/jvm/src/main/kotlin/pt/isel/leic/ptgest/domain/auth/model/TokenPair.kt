package pt.isel.leic.ptgest.domain.auth.model

data class TokenPair(
    val accessToken: Token,
    val refreshToken: Token
)
