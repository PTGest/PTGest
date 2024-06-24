package pt.isel.leic.ptgest.http.utils

import org.springframework.http.HttpHeaders
import pt.isel.leic.ptgest.domain.auth.model.TokenPair

fun createTokensHeaders(
    tokens: TokenPair
): HttpHeaders {
    val headers = HttpHeaders()
    headers.add("Authorization", "Bearer ${tokens.accessToken.token}")
    headers.add("Refresh-Token", "Bearer ${tokens.refreshToken.token}")

    return headers
}
