package pt.isel.leic.ptgest.services.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class JwtService(
    private val transactionManager: TransactionManager,
    private val authDomain: AuthDomain
) {

    fun generateToken(
        userId: UUID,
        role: Role,
        expirationDate: Date,
        creationDate: Date
    ): String {
        val claims = HashMap<String, Any>()
        return createToken(claims, userId, role, expirationDate, creationDate)
    }

    fun extractToken(token: String): TokenDetails {
        val claims = getAllClaimsFromToken(token)

        val userId = claims.id
        val role = claims.subject
        val creationDate = claims.issuedAt
        val expirationDate = claims.expiration

        val tokenDetails = TokenDetails(
            userId = UUID.fromString(userId),
            role = Role.valueOf(role),
            creationDate = Date(creationDate.time),
            expirationDate = Date(expirationDate.time)
        )

        validateToken(tokenDetails)

        return tokenDetails
    }

    private fun validateToken(tokenDetails: TokenDetails) {
        transactionManager.run {
            val authRepo = it.userRepo

            val userDetails =
                authRepo.getUserDetails(tokenDetails.userId)
                    ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (userDetails.role != tokenDetails.role) {
                throw AuthError.TokenError.InvalidUserRoleException
            }
        }

        val currentDate = Date()

        if (tokenDetails.expirationDate.before(currentDate) ||
            !authDomain.validateTokenTtl(tokenDetails.creationDate, currentDate)
        ) {
            throw AuthError.TokenError.TokenExpired
        }
    }

    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts.parser().setSigningKey(authDomain.secret).parseClaimsJws(token).body

    private fun createToken(
        claims: Map<String, Any>,
        userId: UUID,
        role: Role,
        expirationDate: Date,
        creationDate: Date
    ): String =
        Jwts.builder().setClaims(claims)
            .setId(userId.toString())
            .setSubject(role.name)
            .setIssuedAt(creationDate)
            .setExpiration(expirationDate)
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, authDomain.secret).compact()
}
