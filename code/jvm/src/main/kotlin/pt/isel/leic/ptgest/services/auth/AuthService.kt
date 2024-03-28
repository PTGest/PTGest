package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val transactionManager: TransactionManager,
    private val authDomain: AuthDomain,
) {

    fun signUpCompany(
        name: String,
        email: String,
        password: String
    ): UUID =
        transactionManager.run {
            val authRepo = it.authRepo
            val passwordHash = authDomain.hashPassword(password)

            val userId = authRepo.createUser(name, email, passwordHash, Role.COMPANY)

            authRepo.createCompany(userId)

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
            val authRepo = it.authRepo
            val passwordHash = authDomain.hashPassword(password)

            val userId = authRepo.createUser(name, email, passwordHash, Role.INDEPENDENT_TRAINER)

            authRepo.createIndependentTrainer(
                userId,
                gender,
                phoneNumber
            )

            return@run userId
        }

    fun login(email: String, password: String): Token {
        val userDetails = transactionManager.run {
            val authRepo = it.authRepo
            val user = authRepo.getUserDetails(email)
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
}