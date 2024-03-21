package pt.isel.leic.ptgest.domain.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class AuthDomain(
    private val tokenEncoder: TokenEncoder,
    private val passwordEncoder: PasswordEncoder,
    private val config: AuthDomainConfig
) {

    val tokenTtl = config.tokenTTL
    val rollingTtl = config.tokenRollingTTL

    fun hashPassword(password: String): String = passwordEncoder.encode(password)

    fun validatePassword(password: String, passwordHash: String) =
        passwordEncoder.matches(password, passwordHash)

    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun hashToken(token: String): String = tokenEncoder.hashToken(token)

    val tokenLimitPerUser = config.tokenLimitPerUser
}