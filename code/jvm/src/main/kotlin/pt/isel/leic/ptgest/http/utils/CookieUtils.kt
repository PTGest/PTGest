package pt.isel.leic.ptgest.http.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.util.*

// check if we need or is recommended to use the secure attribute
fun setCookie(
    name: String,
    value: String,
    expirationDate: Date,
    httpOnly: Boolean,
    response: HttpServletResponse
) {
    val cookie = Cookie(name, value)
    cookie.maxAge = expirationDate.toInstant().epochSecond.toInt()
    cookie.isHttpOnly = httpOnly
    response.addCookie(cookie)
}

fun revokeCookie(name: String, httpOnly: Boolean, response: HttpServletResponse) {
    val cookie = Cookie(name, null)
    cookie.maxAge = 0
    cookie.isHttpOnly = httpOnly
    response.addCookie(cookie)
}
