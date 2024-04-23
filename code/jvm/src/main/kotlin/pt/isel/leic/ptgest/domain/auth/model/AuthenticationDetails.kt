package pt.isel.leic.ptgest.domain.auth.model

import pt.isel.leic.ptgest.domain.common.Role

data class AuthenticationDetails(
    val role: Role,
    val tokens: TokenPair
)
