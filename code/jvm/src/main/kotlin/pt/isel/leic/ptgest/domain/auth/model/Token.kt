package pt.isel.leic.ptgest.domain.auth.model

import java.util.Date

data class Token(
    val token: String,
    val expirationDate: Date
)
