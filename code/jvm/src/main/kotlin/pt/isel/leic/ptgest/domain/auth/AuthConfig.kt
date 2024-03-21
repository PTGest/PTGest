package pt.isel.leic.ptgest.domain.auth


/**
 * TOKEN_TTL: The time to live of a token in days.
 * ROLLING_TTL: The time to live of a token in days.
 */
object AuthConfig {
    const val TOKEN_TTL: Long = 365
    const val ROLLING_TTL: Long = 7
    const val USER_TOKEN_LIMIT = 3
}