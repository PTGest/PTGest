package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.http.utils.validateStrings
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.time.LocalDate
import java.util.*

@Service
class AuthService(
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

    //  TODO: Verify the quantity of tokens that a user can have
    fun login(email: String, password: String): Token =
        transactionManager.run {
            val authRepo = it.authRepo
            val user = authRepo.getUserDetails(email)
                ?: throw AuthError.UserAuthenticationError.UserNotFound

            if (!authDomain.validatePassword(password, user.passwordHash)) {
                throw AuthError.UserAuthenticationError.InvalidPassword
            }

            val tokenValue = authDomain.generateTokenValue()
            val now = LocalDate.now()
            val expirationDate = now.plusDays(authDomain.rollingTtl)

            authRepo.createToken(
                user.id,
                authDomain.hashToken(tokenValue),
                now,
                expirationDate
            )

            return@run Token(
                token = tokenValue,
                expirationDate = expirationDate
            )
        }


    fun logout() {
        throw NotImplementedError()
    }

    fun validateToken(token: String) {
        transactionManager.run {
            validateStrings(token)

            val authRepo = it.authRepo
            val tokenHash = authDomain.hashToken(token)

            val tokenDetails = authRepo.getToken(tokenHash)
                ?: throw AuthError.TokenError.TokenNotFound

            if (tokenDetails.creationDate.plusDays(authDomain.tokenTtl).isBefore(LocalDate.now())) {
                authRepo.revokeToken(tokenHash)
                throw AuthError.TokenError.TokenExpired
            } else {
                if (tokenDetails.expirationDate.isBefore(LocalDate.now())) {
                    authRepo.revokeToken(tokenHash)
                    throw AuthError.TokenError.TokenExpired
                } else {
                    authRepo.updateExpirationDate(
                        tokenHash,
                        LocalDate.now().plusDays(authDomain.rollingTtl)
                    )
                }
            }
        }
    }

    fun getUserIdByToken(token: String): AuthenticatedUser =
        transactionManager.run {
            validateStrings(token)

            val authRepo = it.authRepo
            val tokenHash = authDomain.hashToken(token)

            val tokenDetails = authRepo.getToken(tokenHash)
                ?: throw AuthError.TokenError.TokenNotFound

            return@run AuthenticatedUser(
                tokenDetails.userId,
                tokenDetails.role
            )
        }

}