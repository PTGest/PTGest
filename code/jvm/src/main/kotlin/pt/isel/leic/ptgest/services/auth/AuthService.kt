package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.Token
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

    fun login(email: String, password: String): Token {
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

        val expirationDate = authDomain.createTokenExpirationDate(currentDate)

        val tokenValue = jwtService.generateToken(
            userId = userDetails.id,
            role = userDetails.role,
            expirationDate = expirationDate,
            creationDate = currentDate
        )

        return Token(
            tokenValue,
            expirationDate
        )
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
}
