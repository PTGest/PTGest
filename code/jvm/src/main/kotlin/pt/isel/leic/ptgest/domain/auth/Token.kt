package pt.isel.leic.ptgest.domain.auth

/**
 * Represents a token.
 * Used for sending tokens to the client.
 */
data class Token(
    val value: String,
    val userId: Int,
    val expirationAge: Long,
    val createdAt: Long,
    val lastUsedAt: Long
)