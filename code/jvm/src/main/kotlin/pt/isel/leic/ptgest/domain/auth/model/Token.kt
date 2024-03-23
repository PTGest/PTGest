package pt.isel.leic.ptgest.domain.auth.model

import java.time.LocalDate


data class Token(
    val token: String,
    val expirationDate: LocalDate
)