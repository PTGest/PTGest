package pt.isel.leic.ptgest.domain.auth


data class AuthDomainConfig(
    val tokenSizeInBytes: Int,
    val tokenTTL: Long,
    val tokenRollingTTL: Long,
    val tokenLimitPerUser: Int
) {
    init {
        require(tokenSizeInBytes > 0)
        require(tokenTTL > 0)
        require(tokenRollingTTL > 0)
        require(tokenLimitPerUser > 0)
    }
}