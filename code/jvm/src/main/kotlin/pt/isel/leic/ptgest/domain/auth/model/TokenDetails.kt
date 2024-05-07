package pt.isel.leic.ptgest.domain.auth.model

import java.util.Date
import java.util.UUID

data class TokenDetails(
    val userId: UUID,
    val expiration: Date
)
