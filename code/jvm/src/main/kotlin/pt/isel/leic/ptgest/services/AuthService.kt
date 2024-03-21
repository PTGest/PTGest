package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.http.utils.validateStrings
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.AuthError
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

            val userId = authRepo.createUser(name, email, passwordHash)

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

            val userId = authRepo.createUser(name, email, passwordHash)

            authRepo.createIndependentTrainer(
                userId,
                gender,
                phoneNumber
            )

            return@run userId
        }

    fun login(email: String, password: String): TokenDetails {
//        validateStrings(email, password)
//        val user = User(1, "Pedro Macedo", "123456jjjjaaaaaaa") // depois ir buscar os dados a base de dados
//
//        if (!authDomain.validatePassword(password, user.passwordHash)) {
//            throw AuthError.UserAuthenticationError.InvalidPassword
//        }
//
//        val tokenValue = authDomain.generateTokenValue()
//
////        val now =
//
//        val token = HashedToken(
//            hash = authDomain.hashToken(tokenValue),
//            userId = user.id,
//            createdAt = now.epochSeconds,
//            lastUsedAt = now.epochSeconds
//        )
//
//        val expirationAge = authDomain.rollingTtl.inWholeSeconds + authDomain.tokenTtl.inWholeSeconds
//
//        return TokenDetails(
//            tokenValue,
//            user.id,
//            expirationAge,
//            now.epochSeconds,
//            now.epochSeconds
//        )
        throw NotImplementedError()
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