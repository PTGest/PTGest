package pt.isel.leic.ptgest.services.auth

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.auth.model.Token
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.AuthRepo
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.utils.validateStrings
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

    //  TODO: Check if we are doing the right way
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

            checkUserTokensLimit(authRepo, user)

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

    private fun checkUserTokensLimit(authRepo: AuthRepo, user: UserDetails) {
        val userTokens = authRepo.getUserTokens(user.id).sortedBy { tokenDetails ->
            tokenDetails.expirationDate
        }

        if (userTokens.size >= authDomain.tokensPerUser) {
            authRepo.revokeToken(userTokens.first().tokenHash)
        }
    }

    fun logout(userId: UUID, token: String) {
        transactionManager.run {
            val authRepo = it.authRepo
            val tokenHash = authDomain.hashToken(token)

            val tokenDetails = authRepo.getToken(tokenHash)
                ?: throw AuthError.TokenError.TokenNotFound

            if (tokenDetails.userId != userId) {
                throw AuthError.TokenError.TokenOwnershipError
            }

            authRepo.revokeToken(tokenHash)
        }
    }

    fun validateToken(token: String): LocalDate =
        transactionManager.run {
            validateStrings(token)

            val authRepo = it.authRepo
            val tokenHash = authDomain.hashToken(token)

            val tokenDetails = authRepo.getToken(tokenHash)
                ?: throw AuthError.TokenError.TokenNotFound

            if (
                tokenDetails.creationDate.plusDays(authDomain.tokenTtl).isBefore(LocalDate.now()) ||
                tokenDetails.expirationDate.isBefore(LocalDate.now())
            ) {
                authRepo.revokeToken(tokenHash)
                throw AuthError.TokenError.TokenExpired
            } else {
                val updatedExpirationDate = LocalDate.now().plusDays(authDomain.rollingTtl)
                authRepo.updateExpirationDate(
                    tokenHash,
                    updatedExpirationDate
                )

                return@run updatedExpirationDate
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
                token,
                tokenDetails.role
            )
        }

}