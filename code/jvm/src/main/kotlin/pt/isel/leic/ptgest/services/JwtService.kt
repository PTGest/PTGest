package pt.isel.leic.ptgest.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.model.AuthTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.AuthError
import java.util.*

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

        val version = transactionManager.run {
            val authRepo = it.authRepo
            it.validateUser(userId, role)

            val version = authRepo.getTokenVersion(userId)
            if (version == null) {
                authRepo.createTokenVersion(userId)
            }

            return@run version ?: 1
        }

        claims["version"] = version

        require(currentDate.before(expirationDate)) { "Expiration date must be after current date" }

        return createToken(claims, userId, role, expirationDate)
    }

    fun extractToken(token: String): AuthTokenDetails {
        val claims = getAllClaimsFromToken(token)

        val expirationDate = claims.expiration
        val version = claims["version"] as Int

        val authTokenDetails = AuthTokenDetails(
            userId = UUID.fromString(claims.id),
            role = Role.valueOf(claims.subject),
            expirationDate = Date(expirationDate.time),
            version = version
        )

        transactionManager.run {
            val authRepo = it.authRepo
            it.validateUser(authTokenDetails.userId, authTokenDetails.role)

            if (authRepo.getTokenVersion(authTokenDetails.userId) != version) {
                throw AuthError.UserAuthenticationError.InvalidTokenVersion
            }
        }

        return authTokenDetails
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

    private fun Transaction.validateUser(userId: UUID, role: Role) {
        val userDetails =
            userRepo.getUserDetails(userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

        if (userDetails.role != role) {
            throw AuthError.UserAuthenticationError.InvalidUserRoleException
        }
    }
}
