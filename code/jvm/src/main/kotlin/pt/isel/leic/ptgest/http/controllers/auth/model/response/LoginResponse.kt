package pt.isel.leic.ptgest.http.controllers.auth.model.response

import java.util.*

internal data class LoginResponse(
    val token: String,
    val expirationDate: Date
)
