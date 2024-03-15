package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.Token
import pt.isel.leic.ptgest.http.utils.validateStrings
import kotlinx.datetime.Clock
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.HashedToken
import pt.isel.leic.ptgest.domain.user.User
import pt.isel.leic.ptgest.services.errors.AuthError

@Service
class AuthServices (
    private val authDomain: AuthDomain,
    private val clock : Clock
) {


    fun signUp(username: String, password: String): Int {
        validateStrings(username, password)

        val passwordHash = authDomain.hashPassword(password)
        return  1
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

        //depois fazer a chamada a base de dados para a criacao do Token

        val expirationAge = authDomain.rollingTtl.inWholeSeconds + authDomain.tokenTtl.inWholeSeconds

        return Token(
            tokenValue,
            user.id,
            expirationAge,
            now.epochSeconds,
            now.epochSeconds
        )
    }
}