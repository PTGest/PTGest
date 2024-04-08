package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.RefreshTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val authDomain: AuthDomain,
    private val transactionManager: TransactionManager
) {

    fun signUpCompany(
        name: String,
        email: String,
        password: String
    ): UUID =
        transactionManager.run {
            val userRepo = it.userRepo

            val userId = createUser(userRepo, name, email, password, Role.COMPANY)

            userRepo.createCompany(userId)

            return@run userId
        }

    fun signUpIndependentTrainer(
        name: String,
        email: String,
        password: String,
        gender: Gender,
        phoneNumber: String?
    ): UUID =
        transactionManager.run {
            val userRepo = it.userRepo

            val userId = createUser(userRepo, name, email, password, Role.INDEPENDENT_TRAINER)

            userRepo.createIndependentTrainer(
                userId,
                gender,
                phoneNumber
            )

            return@run userId
        }

    fun login(email: String, password: String): TokenPair {
        val userDetails = transactionManager.run {
            val userRepo = it.userRepo
            val user = userRepo.getUserDetails(email)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (!authDomain.validatePassword(password, user.passwordHash)) {
                throw AuthError.UserAuthenticationError.InvalidPassword
            }

            return@run user
        }
        val currentDate = Date()

        val tokens = createTokens(currentDate, userDetails.id, userDetails.role)
        val refreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            it.userRepo.createRefreshToken(
                userDetails.id,
                refreshTokenHash,
                tokens.refreshToken.expirationDate
            )
        }
        return tokens
    }

    fun refreshToken(accessToken: String, refreshToken: String): TokenPair {
        val currentDate = Date()

        val accessTokenDetails = jwtService.extractToken(accessToken)
        val refreshTokenDetails = getRefreshTokenDetails(
            authDomain.hashToken(refreshToken),
            accessTokenDetails.userId
        )

        if (refreshTokenDetails.expiration.before(currentDate)) {
            throw AuthError.TokenError.TokenExpired
        }

        val tokens = createTokens(currentDate, accessTokenDetails.userId, accessTokenDetails.role)
        val newRefreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            val userRepo = it.userRepo
            userRepo.removeRefreshToken(refreshToken)

            userRepo.createRefreshToken(
                accessTokenDetails.userId,
                newRefreshTokenHash,
                tokens.refreshToken.expirationDate
            )
        }

        return tokens
    }

    private fun createUser(
        userRepo: UserRepo,
        name: String,
        email: String,
        password: String,
        role: Role
    ): UUID {
        if (userRepo.getUserDetails(email) != null) {
            throw AuthError.UserRegistrationError.UserAlreadyExists
        }

        val passwordHash = authDomain.hashPassword(password)

        return userRepo.createUser(name, email, passwordHash, role)
    }

    private fun createTokens(currentDate: Date, userId: UUID, userRole: Role): TokenPair {
        val accessExpirationDate = authDomain.createAccessTokenExpirationDate(currentDate)
        val refreshExpirationDate = authDomain.createRefreshTokenExpirationDate(currentDate)

        val refreshTokenValue = authDomain.generateTokenValue()

        val accessTokenValue = jwtService.generateToken(
            userId = userId,
            role = userRole,
            expirationDate = accessExpirationDate,
            currentDate = currentDate
        )

        return TokenPair(
            accessToken = Token(
                accessTokenValue,
                accessExpirationDate
            ),
            refreshToken = Token(
                refreshTokenValue,
                refreshExpirationDate
            )
        )
    }

    private fun getRefreshTokenDetails(tokenHash: String, userId: UUID): RefreshTokenDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            val refreshToken = userRepo.getRefreshTokenDetails(tokenHash)
                ?: throw AuthError.TokenError.InvalidRefreshToken

            if (refreshToken.userId != userId) {
                throw AuthError.TokenError.UserIdMismatch
            }

            return@run refreshToken
        }
}
