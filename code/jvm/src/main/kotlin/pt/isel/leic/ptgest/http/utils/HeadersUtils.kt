package pt.isel.leic.ptgest.http.utils

import org.springframework.http.HttpHeaders
import pt.isel.leic.ptgest.domain.auth.model.TokenPair

fun createTokensHeaders(
    tokens: TokenPair
): HttpHeaders {
    val headers = HttpHeaders()
    headers.add("Authorization", "Bearer access_token=${tokens.accessToken.token}")
    headers.add("Authorization", "Bearer refresh_token=${tokens.refreshToken.token}")

    return headers
}
