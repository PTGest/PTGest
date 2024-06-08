package pt.isel.leic.ptgest.http.model.auth.response

import pt.isel.leic.ptgest.domain.auth.model.AuthenticationDetails
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.user.Role

internal data class LoginResponse(
    val role: Role,
    val tokens: TokenPair
) {
    constructor(authenticationDetails: AuthenticationDetails) : this(
        authenticationDetails.role,
        authenticationDetails.tokens
    )
}
