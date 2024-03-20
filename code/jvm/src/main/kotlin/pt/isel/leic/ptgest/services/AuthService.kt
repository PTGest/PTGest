package pt.isel.leic.ptgest.services

import kotlinx.datetime.Clock
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.Token
import pt.isel.leic.ptgest.domain.auth.model.HashedToken
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.user.User
import pt.isel.leic.ptgest.http.utils.validateStrings
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.AuthError
import java.util.*

@Service
class AuthService(
    private val transactionManager: TransactionManager,
    private val authDomain: AuthDomain,
    private val clock: Clock
) {

    fun signUpCompany(name: String, email: String, password: String) {
        throw NotImplementedError()
    }

    fun signUpIndependentTrainer(
        name: String,
        email: String,
        password: String,
        gender: Gender,
        phoneNumber: String?
    ) {
        throw NotImplementedError()
    }

    fun signUpHiredTrainer(
        name: String,
        email: String,
        gender: Gender,
        phoneNumber: String?
    ) {
        throw NotImplementedError()
    }

    fun signUpTrainee(
        name: String,
        email: String,
        birthdate: Date,
        gender: Gender,
        phoneNumber: String?
    ) {
        throw NotImplementedError()
    }

    fun login(email: String, password: String): Token {
        validateStrings(email, password)
        val user = User(1, "Pedro Macedo", "123456jjjjaaaaaaa") // depois ir buscar os dados a base de dados

        if (!authDomain.validatePassword(password, user.passwordHash)) {
            throw AuthError.UserAuthenticationError.InvalidPassword
        }

        val tokenValue = authDomain.generateTokenValue()

        val now = clock.now()

        val token = HashedToken(
            hash = authDomain.hashToken(tokenValue),
            userId = user.id,
            createdAt = now.epochSeconds,
            lastUsedAt = now.epochSeconds
        )

        val expirationAge = authDomain.rollingTtl.inWholeSeconds + authDomain.tokenTtl.inWholeSeconds

        return Token(
            tokenValue,
            user.id,
            expirationAge,
            now.epochSeconds,
            now.epochSeconds
        )
    }

    fun logout() {
        throw NotImplementedError()
    }

    fun getUserIdByToken(token: String): Int? {
//        transactionManager.run {
//            validateStrings(token)
//            val authRepo = it.authRepo
//            val tokenHash = authDomain.hashToken(token)
//            val tokenDetails = authRepo.getToken(tokenHash) ?: return@run null
//            return@run tokenDetails.userId
//        }
        throw NotImplementedError()
    }

    fun updateLastUsedAt(token: String): Long {
        throw NotImplementedError()
    }
}