package pt.isel.leic.ptgest.domain.auth

import kotlin.time.Duration

data class AuthDomainConfig(
    val tokenSizeInBytes: Int,
    val tokenTTL: Duration,
    val tokenRollingTTL: Duration,
    val tokenLimitPerUser: Int
) {
    init {
        require(tokenSizeInBytes > 0)
        require(tokenTTL.isPositive())
        require(tokenRollingTTL.isPositive())
        require(tokenLimitPerUser > 0)
    }
}