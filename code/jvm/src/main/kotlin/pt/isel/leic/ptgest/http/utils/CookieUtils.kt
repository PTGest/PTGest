package pt.isel.leic.ptgest.http.utils

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import java.util.Date

fun setCookies(response: HttpServletResponse, tokens: TokenPair, currentDate: Date) {
    setCookie(
        "access_token",
        tokens.accessToken.token,
        currentDate,
        tokens.accessToken.expirationDate,
        response
    )

    setCookie(
        "refresh_token",
        tokens.refreshToken.token,
        currentDate,
        tokens.refreshToken.expirationDate,
        response
    )
}

fun revokeCookies(response: HttpServletResponse) {
    revokeCookie(
        "access_token",
        response
    )

    revokeCookie(
        "refresh_token",
        response
    )
}

private fun setCookie(
    name: String,
    value: String,
    currentDate: Date,
    expirationDate: Date,
    response: HttpServletResponse
) {
    val responseCookie = ResponseCookie.from(name, value)
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .path("/api")
        .maxAge((expirationDate.time - currentDate.time) / 1000)
        .build()

    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString())
}

private fun revokeCookie(name: String, response: HttpServletResponse) {
    val responseCookie = ResponseCookie.from(name, "")
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .path("/api")
        .maxAge(0)
        .build()

    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString())
}
