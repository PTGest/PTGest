package pt.isel.leic.ptgest.http.controllers.auth.model.response

import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.common.Role

internal data class LoginResponse(
    val role: Role,
    val tokens: TokenPair
)
