package pt.isel.leic.ptgest.http.controllers.auth.model.response

import java.time.LocalDate

internal data class LoginResponse(
    val token: String,
    val expirationDate: LocalDate
)
