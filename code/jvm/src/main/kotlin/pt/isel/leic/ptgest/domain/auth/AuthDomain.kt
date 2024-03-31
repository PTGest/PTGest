package pt.isel.leic.ptgest.domain.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthDomain(
    private val passwordEncoder: PasswordEncoder
) {

    val secret: String
        get() = System.getenv("SECRET")
            ?: throw IllegalStateException("Missing environment variable SECRET")

    fun validateTokenTtl(tokenCreationDate: Date, currentDate: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = tokenCreationDate
        calendar.add(Calendar.YEAR, 1)

        return currentDate.before(calendar.time)
    }

    fun updateTokenExpirationDate(
        tokenCreationDate: Date,
        currentDate: Date
    ): Date {
        val expirationDate = createTokenExpirationDate(currentDate)

        val calendarTtl = Calendar.getInstance()
        calendarTtl.time = tokenCreationDate
        calendarTtl.add(Calendar.YEAR, 1)

        return if (expirationDate.before(calendarTtl.time)) {
            expirationDate
        } else {
            calendarTtl.time
        }
    }

    fun createTokenExpirationDate(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        return calendar.time
    }

    fun hashPassword(password: String): String = passwordEncoder.encode(password)

    fun validatePassword(password: String, passwordHash: String) =
        passwordEncoder.matches(password, passwordHash)
}
