package pt.isel.leic.ptgest.http.controllers.auth.model.response

import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

internal data class LoginResponse(
    val userId: UUID,
    val role: Role,
    val tokens: TokenPair
)
