package pt.isel.leic.ptgest.domain.auth.model

import java.util.*

data class UserDetails(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordHash: String
)
