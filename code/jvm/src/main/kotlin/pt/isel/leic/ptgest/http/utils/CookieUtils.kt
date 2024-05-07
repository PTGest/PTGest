package pt.isel.leic.ptgest.http.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
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
    val cookie = Cookie(name, value)
    val currentInSeconds = currentDate.time / 1000
    val expirationInSeconds = expirationDate.time / 1000
    cookie.maxAge = (expirationInSeconds - currentInSeconds).toInt()
    cookie.isHttpOnly = true
    cookie.secure = true
    response.addCookie(cookie)
}

private fun revokeCookie(name: String, response: HttpServletResponse) {
    val cookie = Cookie(name, null)
    cookie.maxAge = 0
    cookie.isHttpOnly = true
    cookie.secure = true
    response.addCookie(cookie)
}
