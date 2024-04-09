package pt.isel.leic.ptgest.domain.auth.model

import java.util.*

data class TokenDetails(
    val userId: UUID,
    val expiration: Date
)
