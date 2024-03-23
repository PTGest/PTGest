package pt.isel.leic.ptgest.http.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.time.LocalDate

//check if we need or is recommended to use the secure attribute
fun setCookie(
    name: String,
    value: String,
    expirationDate: LocalDate,
    httpOnly: Boolean,
    response: HttpServletResponse
) {
    val cookie = Cookie(name, value)
    cookie.maxAge = expirationDate.toEpochDay().toInt()
    cookie.isHttpOnly = httpOnly
    response.addCookie(cookie)
}

fun removeCookie(name: String, httpOnly: Boolean, response: HttpServletResponse) {
    val cookie = Cookie(name, null)
    cookie.maxAge = 0
    cookie.isHttpOnly = httpOnly
    response.addCookie(cookie)
}
