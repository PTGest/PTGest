package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticationDetails
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.MailService
import java.util.Date
import java.util.UUID

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
            val authRepo = it.authRepo

            val userId = it.createUser(
                name,
                email,
                password,
                Role.COMPANY
            )
            authRepo.createCompany(userId)
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
            val authRepo = it.authRepo

            val userId = it.createUser(
                name,
                email,
                password,
                Role.INDEPENDENT_TRAINER
            )
            authRepo.createTrainer(userId, gender, phoneNumber)
        }
    }

    fun signUpHiredTrainer(
        companyId: UUID,
        userRole: Role,
        name: String,
        email: String,
        gender: Gender,
        capacity: Int,
        phoneNumber: String?
    ): UUID {
        if (userRole != Role.COMPANY) {
            throw AuthError.UserAuthenticationError.UnauthorizedRole
        }

        require(capacity > 0) { "Invalid capacity must be greater than 0." }

        val trainerId = transactionManager.run {
            val authRepo = it.authRepo

            val tempPassword = authDomain.generateTokenValue()
            val tempPasswordHash = authDomain.hashPassword(tempPassword)

            val trainerId = it.createUser(
                name,
                email,
                tempPasswordHash,
                Role.HIRED_TRAINER
            )

            authRepo.createTrainer(trainerId, gender, phoneNumber)
            authRepo.createCompanyTrainer(companyId, trainerId, capacity)
            return@run trainerId
        }

        reSetPassword(email, true)
        return trainerId
    }

    fun signUpTrainee(
        userId: UUID,
        userRole: Role,
        name: String,
        email: String,
        birthdate: Date,
        gender: Gender,
        phoneNumber: String?
    ): UUID {
        require(birthdate.before(Date())) { "Invalid birthdate." }

        val traineeId = transactionManager.run {
            val authRepo = it.authRepo

            val tempPassword = authDomain.generateTokenValue()
            val tempPasswordHash = authDomain.hashPassword(tempPassword)

            val traineeId = it.createUser(
                name,
                email,
                tempPasswordHash,
                Role.TRAINEE
            )

            authRepo.createTrainee(traineeId, birthdate, gender, phoneNumber)

            it.associateTrainee(userId, userRole, traineeId)
            return@run traineeId
        }
        reSetPassword(email, true)
        return traineeId
    }

    fun reSetPassword(email: String, setPassword: Boolean = false) {
        val userDetails = transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getUserDetails(email)
                ?: throw AuthError.UserAuthenticationError.UserNotFound
        }

        val token = authDomain.generateTokenValue()
        val tokenHash = authDomain.hashToken(token)
        val tokenExpiration = authDomain.createPasswordResetTokenExpirationDate(Date())

        transactionManager.run {
            val authRepo = it.authRepo

            authRepo.removeOldPasswordResetTokens(userDetails.id)

            authRepo.createToken(
                tokenHash,
                userDetails.id,
                tokenExpiration
            )

            authRepo.createPasswordResetToken(tokenHash)
        }

        mailService.sendMail(
            email,
            "PTGest - Password reset",
            "Click the following link to ${if (setPassword) "set" else "reset"} your password:\n" +
                "http://localhost:5173/resetPassword/$token"
        )
    }

    fun validatePasswordResetToken(token: String) {
        require(token.isNotBlank()) { "Invalid token." }

        val tokenHash = authDomain.hashToken(token)

        transactionManager.run {
            val authRepo = it.authRepo

            val resetToken = authRepo.getPasswordResetToken(tokenHash)
                ?: throw AuthError.TokenError.InvalidPasswordResetToken

            if (resetToken.expiration.before(Date())) {
                throw AuthError.TokenError.TokenExpired
            }
        }
    }

    fun resetPassword(token: String, password: String) {
        require(token.isNotBlank()) { "Invalid token." }

        val currentDate = Date()
        val tokenHash = authDomain.hashToken(token)

        val userDetails = transactionManager.run {
            val authRepo = it.authRepo
            val userRepo = it.userRepo

            val resetToken = authRepo.getPasswordResetToken(tokenHash)
                ?: throw AuthError.TokenError.InvalidPasswordResetToken
            val userDetails = userRepo.getUserDetails(resetToken.userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (resetToken.expiration.before(Date())) {
                throw AuthError.TokenError.TokenExpired
            }

            val passwordHash = authDomain.hashPassword(password)

            authRepo.resetPassword(resetToken.userId, passwordHash)

            return@run userDetails
        }

        mailService.sendMail(
            userDetails.email,
            "PTGest - Password reset successful",
            "Your password has been reset at $currentDate."
        )
    }

    fun login(currentDate: Date, email: String, password: String): AuthenticationDetails {
        val userDetails = transactionManager.run {
            val userRepo = it.userRepo

            val user = userRepo.getUserDetails(email)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (!authDomain.validatePassword(password, user.passwordHash)) {
                throw AuthError.UserAuthenticationError.InvalidPassword
            }

            return@run user
        }

        val tokens = createTokens(currentDate, userDetails.id, userDetails.role)
        val refreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            val authRepo = it.authRepo

            authRepo.createToken(
                refreshTokenHash,
                userDetails.id,
                tokens.refreshToken.expirationDate
            )
            authRepo.createRefreshToken(refreshTokenHash)
        }

        return AuthenticationDetails(
            role = userDetails.role,
            tokens = tokens
        )
    }

    fun refreshToken(accessToken: String, refreshToken: String, currentDate: Date): TokenPair {
        val refreshTokenHash = authDomain.hashToken(refreshToken)
        val accessTokenDetails = jwtService.extractToken(accessToken)
        val refreshTokenDetails = transactionManager.run {
            it.getRefreshTokenDetails(
                refreshTokenHash,
                accessTokenDetails.userId
            )
        }

        if (refreshTokenDetails.expiration.before(currentDate)) {
            throw AuthError.TokenError.TokenExpired
        }

        val tokens = createTokens(currentDate, accessTokenDetails.userId, accessTokenDetails.role)
        val newRefreshTokenHash = authDomain.hashToken(tokens.refreshToken.token)

        transactionManager.run {
            val authRepo = it.authRepo
            authRepo.removeToken(refreshTokenHash)

            authRepo.createToken(
                newRefreshTokenHash,
                accessTokenDetails.userId,
                tokens.refreshToken.expirationDate
            )
            authRepo.createRefreshToken(
                newRefreshTokenHash
            )
        }
        return tokens
    }

    fun validateRefreshToken(userId: UUID, refreshToken: String) {
        require(refreshToken.isNotBlank()) { "Invalid refresh token." }

        val refreshTokenHash = authDomain.hashToken(refreshToken)

        transactionManager.run {
            val authRepo = it.authRepo

            val refreshTokenDetails = authRepo.getRefreshTokenDetails(refreshTokenHash)
                ?: throw AuthError.TokenError.InvalidRefreshToken

            if (refreshTokenDetails.expiration.before(Date())) {
                throw AuthError.TokenError.TokenExpired
            }

            if (refreshTokenDetails.userId != userId) {
                throw AuthError.TokenError.UserIdMismatch
            }
        }
    }

    fun logout(refreshToken: String) {
        transactionManager.run {
            val authRepo = it.authRepo

            authRepo.removeToken(authDomain.hashToken(refreshToken))
        }
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

    private fun Transaction.createUser(
        name: String,
        email: String,
        password: String,
        role: Role
    ): UUID {
        if (userRepo.getUserDetails(email) != null) {
            throw AuthError.UserRegistrationError.UserAlreadyExists
        }

        val passwordHash = authDomain.hashPassword(password)

        return authRepo.createUser(name, email, passwordHash, role)
    }

    private fun Transaction.getRefreshTokenDetails(tokenHash: String, userId: UUID): TokenDetails {
        val refreshToken = authRepo.getRefreshTokenDetails(tokenHash)
            ?: throw AuthError.TokenError.InvalidRefreshToken

        if (refreshToken.userId != userId) {
            throw AuthError.TokenError.UserIdMismatch
        }

        return refreshToken
    }

    private fun Transaction.associateTrainee(userId: UUID, userRole: Role, traineeId: UUID) {
        when (userRole) {
            Role.COMPANY -> {
                userRepo.associateTraineeToCompany(
                    traineeId = traineeId,
                    companyId = userId
                )
            }
            Role.INDEPENDENT_TRAINER -> {
                userRepo.associateTraineeToTrainer(
                    traineeId = traineeId,
                    trainerId = traineeId
                )
            }
            else -> throw AuthError.UserAuthenticationError.UnauthorizedRole
        }
    }
}
