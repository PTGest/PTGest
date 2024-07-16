package pt.isel.leic.ptgest.http.model.auth.response

import pt.isel.leic.ptgest.domain.auth.model.AuthenticationDetails
import pt.isel.leic.ptgest.domain.user.Role
import java.util.*

data class LoginResponse(
    val userId: UUID,
    val role: Role,
    val accessTokenExpirationDate: Date,
    val refreshTokenExpirationDate: Date
) {

    constructor(details: AuthenticationDetails) : this(
        details.id,
        details.role,
        details.tokens.accessToken.expirationDate,
        details.tokens.refreshToken.expirationDate
    )
}
