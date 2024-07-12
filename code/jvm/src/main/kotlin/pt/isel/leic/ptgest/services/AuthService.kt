package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.ServerConfig
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticationDetails
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.AuthError
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
        name: String,
        email: String,
        gender: Gender,
        capacity: Int,
        phoneNumber: String?
    ): UUID {
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

        forgetPassword(email, true)
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
        forgetPassword(email, true)
        return traineeId
    }

    fun forgetPassword(email: String, setPassword: Boolean = false) {
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
            authRepo.createPasswordResetToken(tokenHash, userDetails.id, tokenExpiration)
        }

        mailService.sendMail(
            email,
            "PTGest - Password reset",
            "Click the following link to ${if (setPassword) "set" else "reset"} your password:\n" +
                "${ServerConfig.frontendUrl}/resetPassword/$token"
        )
    }

    fun validatePasswordResetRequest(requestToken: String) {
        require(requestToken.isNotBlank()) { "Invalid token." }

        val tokenHash = authDomain.hashToken(requestToken)

        transactionManager.run {
            val authRepo = it.authRepo

            val resetToken = authRepo.getPasswordResetRequest(tokenHash)
                ?: throw AuthError.UserAuthenticationError.InvalidPasswordResetRequest

            if (resetToken.expiration.before(Date())) {
                throw AuthError.UserAuthenticationError.PasswordRequestExpired
            }
        }
    }

    fun resetPassword(requestToken: String, password: String) {
        require(requestToken.isNotBlank()) { "Invalid token." }

        val currentDate = Date()
        val tokenHash = authDomain.hashToken(requestToken)

        val userDetails = transactionManager.run {
            val authRepo = it.authRepo
            val userRepo = it.userRepo

            val resetToken = authRepo.getPasswordResetRequest(tokenHash)
                ?: throw AuthError.UserAuthenticationError.InvalidPasswordResetRequest

            val userDetails = userRepo.getUserDetails(resetToken.userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (resetToken.expiration.before(Date())) {
                throw AuthError.UserAuthenticationError.PasswordRequestExpired
            }

            val passwordHash = authDomain.hashPassword(password)

            authRepo.resetPassword(resetToken.userId, passwordHash)
            authRepo.revokePasswordResetToken(tokenHash)

            if (authRepo.getTokenVersion(resetToken.userId) != null) {
                authRepo.updateTokenVersion(resetToken.userId)
            }

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

        return AuthenticationDetails(
            id = userDetails.id,
            role = userDetails.role,
            tokens = tokens
        )
    }

    fun refreshToken(refreshToken: String, currentDate: Date): TokenPair {
        val refreshTokenDetails = jwtService.extractToken(refreshToken)

        return createTokens(currentDate, refreshTokenDetails.userId, refreshTokenDetails.role)
    }

    fun changePassword(userId: UUID, currentPassword: String, newPassword: String) {
        transactionManager.run {
            val authRepo = it.authRepo
            val userRepo = it.userRepo

            val user = userRepo.getUserDetails(userId)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (!authDomain.validatePassword(currentPassword, user.passwordHash)) {
                throw AuthError.UserAuthenticationError.InvalidPassword
            }

            val newPasswordHash = authDomain.hashPassword(newPassword)

            authRepo.resetPassword(userId, newPasswordHash)
            authRepo.updateTokenVersion(userId)
        }
    }

    private fun createTokens(currentDate: Date, userId: UUID, userRole: Role): TokenPair {
        val accessExpirationDate = authDomain.createAccessTokenExpirationDate(currentDate)
        val refreshExpirationDate = authDomain.createRefreshTokenExpirationDate(currentDate)

        val accessTokenValue = jwtService.generateToken(
            userId = userId,
            role = userRole,
            expirationDate = accessExpirationDate,
            currentDate = currentDate
        )

        val refreshTokenValue = jwtService.generateToken(
            userId = userId,
            role = userRole,
            expirationDate = refreshExpirationDate,
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
        if (userRepo.userExists(email)) {
            throw AuthError.UserRegistrationError.UserAlreadyExists
        }

        val passwordHash = authDomain.hashPassword(password)

        return authRepo.createUser(name, email, passwordHash, role)
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
                    trainerId = userId
                )
            }

            else -> throw AuthError.UserAuthenticationError.UnauthorizedRole
        }
    }
}
