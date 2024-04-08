package pt.isel.leic.ptgest.domain.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class AuthDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder
) {
    fun createAccessTokenExpirationDate(currentDate: Date): Date =
        createExpirationDate(currentDate, Calendar.HOUR, 8)

    fun createRefreshTokenExpirationDate(currentDate: Date): Date =
        createExpirationDate(currentDate, Calendar.WEEK_OF_YEAR, 1)

    private fun createExpirationDate(currentDate: Date, units: Int, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(units, amount)
        return calendar.time
    }

    fun hashPassword(password: String): String = passwordEncoder.encode(password)

    fun hashToken(token: String): String = tokenEncoder.hashToken(token)

    fun generateTokenValue(): String =
        ByteArray(32).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun validatePassword(password: String, passwordHash: String) =
        passwordEncoder.matches(password, passwordHash)
}
