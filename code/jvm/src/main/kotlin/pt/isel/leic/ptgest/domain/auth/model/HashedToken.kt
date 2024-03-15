package pt.isel.leic.ptgest.domain.auth.model

data class HashedToken(
    val hash: String,
    val userId: Int,
    val createdAt: Long,
    val lastUsedAt: Long
)