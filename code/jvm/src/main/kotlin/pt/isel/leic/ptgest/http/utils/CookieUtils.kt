package pt.isel.leic.ptgest.http.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import java.util.*

fun setCookies(response: HttpServletResponse, tokens: TokenPair) {
    setCookie(
        "access_token",
        tokens.accessToken.token,
        tokens.accessToken.expirationDate,
        response
    )

    setCookie(
        "refresh_token",
        tokens.refreshToken.token,
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
    expirationDate: Date,
    response: HttpServletResponse
) {
    val cookie = Cookie(name, value)
    cookie.maxAge = expirationDate.toInstant().epochSecond.toInt()
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
