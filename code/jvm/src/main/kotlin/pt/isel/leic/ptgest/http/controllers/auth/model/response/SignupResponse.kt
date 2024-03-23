package pt.isel.leic.ptgest.http.controllers.auth.model.response

import java.util.*

data class SignupResponse(
    val username: String,
    val id: UUID
)
