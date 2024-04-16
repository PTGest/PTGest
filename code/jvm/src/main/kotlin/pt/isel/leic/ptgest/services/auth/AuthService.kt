package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticationDetails
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.MailService
import java.util.*

@Service
class AuthService(
    private val authDomain: AuthDomain,
    private val jwtService: JwtService,
    private val mailService: MailService,
    private val transactionManager: TransactionManager
) {

    fun signUpCompany(
        name: String,
        email: String,
        password: String
    ) {
        transactionManager.run {
            val userRepo = it.userRepo
            val userId = createUser(userRepo, name, email, password, Role.COMPANY)
            userRepo.createCompany(userId)
        }
    }

    fun signUpIndependentTrainer(
        name: String,
        email: String,
        password: String,
        gender: Gender,
        phoneNumber: String?
    ) {
        transactionManager.run {
            val userRepo = it.userRepo
            val userId = createUser(userRepo, name, email, password, Role.INDEPENDENT_TRAINER)
            userRepo.createTrainer(userId, gender, phoneNumber?.trim())
        }
    }

    fun signUpHiredTrainer(
        companyId: UUID,
        name: String,
        email: String,
        gender: Gender,
        phoneNumber: String?
    ) {
        transactionManager.run {
            val userRepo = it.userRepo

            val tempPassword = authDomain.generateTokenValue()
            val tempPasswordHash = authDomain.hashPassword(tempPassword)

            val userId = createUser(
                userRepo,
                name,
                email,
                tempPasswordHash,
                Role.HIRED_TRAINER
            )

            userRepo.createTrainer(userId, gender, phoneNumber?.trim())
            userRepo.createCompanyTrainer(companyId, userId)
        }
    }

    fun signUpTrainee(
        name: String,
        email: String,
        birthdate: Date,
        gender: Gender,
        phoneNumber: String?,
        trainerId: UUID? = null
    ) {
        transactionManager.run {
            val userRepo = it.userRepo

            val tempPassword = authDomain.generateTokenValue()
            val tempPasswordHash = authDomain.hashPassword(tempPassword)

            val userId = createUser(
                userRepo,
                name,
                email,
                tempPasswordHash,
                Role.TRAINEE
            )

            userRepo.createTrainee(userId, birthdate, gender, phoneNumber?.trim())
            if (trainerId != null) {
                userRepo.associateTraineeToTrainer(userId, trainerId)
            }
        }
    }

    fun forgetPassword(email: String) {
        val precessedEmail = email.trim()

        val userDetails = transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getUserDetails(precessedEmail)
                ?: throw AuthError.UserAuthenticationError.UserNotFound
        }

        val token = authDomain.generateTokenValue()
        val tokenHash = authDomain.hashToken(token)
        val tokenExpiration = authDomain.createPasswordResetTokenExpirationDate(Date())

        transactionManager.run {
            val userRepo = it.userRepo

            userRepo.createToken(
                tokenHash,
                userDetails.id,
                tokenExpiration
            )
            userRepo.createPasswordResetToken(tokenHash)
        }

        mailService.sendMail(
            precessedEmail,
            "PTGest - Password reset",
            "Click the following link to reset your password:\n" +
                "http://localhost:8080/auth/resetPassword/$token"
        )
    }

    fun validatePasswordResetToken(token: String) {
        val tokenHash = authDomain.hashToken(token.trim())

        transactionManager.run {
            val resetToken = it.userRepo.getPasswordResetToken(tokenHash)
                ?: throw AuthError.TokenError.InvalidPasswordResetToken

            if (resetToken.expiration.before(Date())) {
                throw AuthError.TokenError.TokenExpired
            }
        }
    }

    fun resetPassword(token: String, password: String) {
        val currentDate = Date()
        val tokenHash = authDomain.hashToken(token.trim())

        val userDetails = transactionManager.run {
            val userRepo = it.userRepo

            val resetToken = userRepo.getPasswordResetToken(tokenHash)
                ?: throw AuthError.TokenError.InvalidPasswordResetToken
            val userDetails = userRepo.getUserDetails(resetToken.userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (resetToken.expiration.before(Date())) {
                throw AuthError.TokenError.TokenExpired
            }

            val passwordHash = authDomain.hashPassword(password.trim())

            userRepo.resetPassword(resetToken.userId, passwordHash)

            return@run userDetails
        }

        mailService.sendMail(
            userDetails.email,
            "PTGest - Password reset successful",
            "Your password has been reset at $currentDate."
        )
    }

    fun login(email: String, password: String): AuthenticationDetails {
        val userDetails = transactionManager.run {
            val userRepo = it.userRepo
            val user = userRepo.getUserDetails(email.trim())
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (!authDomain.validatePassword(password.trim(), user.passwordHash)) {
                throw AuthError.UserAuthenticationError.InvalidPassword
            }

            return@run user
        }
        val currentDate = Date()
        val tokens = createTokens(currentDate, userDetails.id, userDetails.role)
        val refreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            val userRepo = it.userRepo
            userRepo.createToken(
                refreshTokenHash,
                userDetails.id,
                tokens.refreshToken.expirationDate
            )
            userRepo.createRefreshToken(refreshTokenHash)
        }

        return AuthenticationDetails(
            userId = userDetails.id,
            role = userDetails.role,
            tokens = tokens
        )
    }

    fun refreshToken(accessToken: String, refreshToken: String): TokenPair {
        val currentDate = Date()

        val refreshTokenHash = authDomain.hashToken(refreshToken.trim())
        val accessTokenDetails = jwtService.extractToken(accessToken.trim())
        val refreshTokenDetails = getRefreshTokenDetails(
            refreshTokenHash,
            accessTokenDetails.userId
        )

        if (refreshTokenDetails.expiration.before(currentDate)) {
            throw AuthError.TokenError.TokenExpired
        }

        val tokens = createTokens(currentDate, accessTokenDetails.userId, accessTokenDetails.role)
        val newRefreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            val userRepo = it.userRepo
            userRepo.removeToken(refreshTokenHash)

            userRepo.createToken(
                newRefreshTokenHash,
                accessTokenDetails.userId,
                tokens.refreshToken.expirationDate
            )
            userRepo.createRefreshToken(
                newRefreshTokenHash
            )
        }

        return tokens
    }

    fun logout(refreshToken: String) {
        transactionManager.run {
            val userRepo = it.userRepo
            userRepo.removeToken(authDomain.hashToken(refreshToken.trim()))
        }
    }

    private fun createUser(
        userRepo: UserRepo,
        name: String,
        email: String,
        password: String,
        role: Role
    ): UUID {
        val processedName = name.trim()
        val processedEmail = email.trim()
        val processedPassword = password.trim()

        if (userRepo.getUserDetails(processedEmail) != null) {
            throw AuthError.UserRegistrationError.UserAlreadyExists
        }

        val passwordHash = authDomain.hashPassword(processedPassword)

        return userRepo.createUser(processedName, processedEmail, passwordHash, role)
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

    private fun getRefreshTokenDetails(tokenHash: String, userId: UUID): TokenDetails =
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
