package pt.isel.leic.ptgest.http.media

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

object HttpResponse {

    fun <T> ok(
        message: String?,
        details: T?,
        headers: HttpHeaders = HttpHeaders()
    ) = httpResponse(message, details, headers, HttpStatus.OK)

    fun ok(
        message: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ) = httpResponse(message, headers, HttpStatus.OK)

    fun created(
        message: String? = null,
        headers: HttpHeaders = HttpHeaders()
    ) = httpResponse(message, headers, HttpStatus.CREATED)

    private fun <T> httpResponse(
        message: String? = null,
        details: T? = null,
        headers: HttpHeaders = HttpHeaders(),
        code: HttpStatusCode
    ) =
        hashMapOf<String, Any?>().apply {
            put("details", details)
            put("message", message ?: getDefaultMessage(code))
        }.let {
            ResponseEntity(it, headers, code)
        }

    private fun httpResponse(
        message: String? = null,
        headers: HttpHeaders = HttpHeaders(),
        code: HttpStatusCode
    ) =
        hashMapOf<String, Any?>().apply {
            put("message", message ?: getDefaultMessage(code))
        }.let {
            ResponseEntity(it, headers, code)
        }

    private fun getDefaultMessage(code: HttpStatusCode): String =
        when (code.value()) {
            200 -> "OK"
            201 -> "Created"
            204 -> "No Content"
            else -> throw IllegalArgumentException("Invalid status code for a successful response ($code).")
        }
}
