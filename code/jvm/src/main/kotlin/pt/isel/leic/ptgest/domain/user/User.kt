package pt.isel.leic.ptgest.domain.user

data class User(
    val id: Int,
    val name: String,
    val passwordHash: String
)