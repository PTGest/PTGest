package pt.isel.leic.ptgest.services.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.model.AccessTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.Date
import java.util.UUID

@Service
class JwtService(
    private val secret: JWTSecret,
    private val transactionManager: TransactionManager
) {

    fun generateToken(
        userId: UUID,
        role: Role,
        expirationDate: Date,
        currentDate: Date
    ): String {
        val claims = HashMap<String, Any>()

        transactionManager.run {
            it.validateUser(userId, role)
        }

        require(
            currentDate.before(expirationDate) ||
                currentDate == expirationDate
        ) { "Expiration date must be after or equal to creation date" }

        return createToken(claims, userId, role, expirationDate)
    }

    fun extractToken(token: String): AccessTokenDetails {
        val claims = getAllClaimsFromToken(token)

        val userId = claims.id
        val role = claims.subject
        val expirationDate = claims.expiration

        val accessTokenDetails = AccessTokenDetails(
            userId = UUID.fromString(userId),
            role = Role.valueOf(role),
            expirationDate = Date(expirationDate.time)
        )

        transactionManager.run {
            it.validateUser(accessTokenDetails.userId, accessTokenDetails.role)
        }

        return accessTokenDetails
    }

    private fun Transaction.validateUser(userId: UUID, role: Role) {
        val userDetails =
            userRepo.getUserDetails(userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

        if (userDetails.role != role) {
            throw AuthError.UserAuthenticationError.InvalidUserRoleException
        }
    }

    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts.parser().setSigningKey(secret.value).parseClaimsJws(token).body

    private fun createToken(
        claims: Map<String, Any>,
        userId: UUID,
        role: Role,
        expirationDate: Date
    ): String =
        Jwts.builder().setClaims(claims)
            .setId(userId.toString())
            .setSubject(role.name)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secret.value).compact()
}
