package pt.isel.leic.ptgest.services.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class JwtService(
    private val authDomain: AuthDomain,
    private val secret: JWTSecret,
    private val transactionManager: TransactionManager
) {

    fun generateToken(
        userId: UUID,
        role: Role,
        expirationDate: Date,
        creationDate: Date
    ): String {
        val claims = HashMap<String, Any>()

        validateUser(userId, role)

        require(creationDate.before(expirationDate)) { "Expiration date must be after creation date" }
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

    private fun validateUser(userId: UUID, role: Role) {
        transactionManager.run {
            val authRepo = it.userRepo

            val userDetails =
                authRepo.getUserDetails(userId)
                    ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (userDetails.role != role) {
                throw AuthError.UserAuthenticationError.InvalidUserRoleException
            }
        }
    }

    private fun validateToken(tokenDetails: TokenDetails) {
        validateUser(tokenDetails.userId, tokenDetails.role)

        val currentDate = Date()

        if (!authDomain.validateTokenTtl(tokenDetails.creationDate, currentDate)) {
            throw AuthError.TokenError.TokenExpired
        }
    }

    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts.parser().setSigningKey(secret.value).parseClaimsJws(token).body

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
            .signWith(SignatureAlgorithm.HS256, secret.value).compact()
}
