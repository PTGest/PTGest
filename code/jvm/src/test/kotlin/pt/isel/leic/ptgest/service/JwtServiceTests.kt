package pt.isel.leic.ptgest.service

import io.jsonwebtoken.ExpiredJwtException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.reset
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.JwtService
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest
class JwtServiceTests {

    @Nested
    inner class GenerateTokenTests {
        @Test
        fun `generate token with no exemptions`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails }

            val token = createToken()

            assert(token.isNotEmpty())
        }

        @Test
        fun `generate token without any user details`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> { createToken() }
        }

        @Test
        fun `generate token with expiration date before creation date`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails }

            val exception = assertFailsWith<IllegalArgumentException> {
                createInvalidToken()
            }

            assertEquals("Expiration date must be after creation date", exception.message)
        }
    }

    @Nested
    inner class ExtractTokenTests {

        @Test
        fun `generate token and extract it successfully`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails }

            val token = createToken()
            val extractedToken = mockJwtService.extractToken(token)

            assertEquals(tokenDetails.userId, extractedToken.userId)
            assertEquals(tokenDetails.role, extractedToken.role)

            assertEquals(
                generateDateToCompare(tokenDetails.creationDate),
                generateDateToCompare(extractedToken.creationDate)
            )
            assertEquals(
                generateDateToCompare(tokenDetails.expirationDate),
                generateDateToCompare(extractedToken.expirationDate)
            )
        }

        @Test
        fun `generate token and extract it with miss match role`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails }

            val token = createToken()

            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails.copy(role = Role.INDEPENDENT_TRAINER) }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidUserRoleException> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `generate token and extract it when token is expired with expiration date`() {
            `when`(mockUserRepo.getUserDetails(tokenDetails.userId))
                .then { userDetails }

            val creationDate = Calendar.getInstance()
                .apply { time = currentDate; add(Calendar.MONTH, -2) }.time
            val expirationDate = Calendar.getInstance()
                .apply { time = currentDate; add(Calendar.MONTH, -1) }.time

            println(creationDate)
            println(expirationDate)

            val token = mockJwtService.generateToken(
                tokenDetails.userId,
                tokenDetails.role,
                expirationDate,
                creationDate
            )

            assertFailsWith<ExpiredJwtException> {
                mockJwtService.extractToken(token)
            }
        }
    }

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo)
    }

    private val currentDate = Date()

    private val userId = UUID.randomUUID()

    private val userDetails = UserDetails(
        id = userId,
        name = "UserTest",
        email = "usertest@mail.com",
        passwordHash = "passwordHashed",
        role = Role.COMPANY
    )

    private val tokenDetails = TokenDetails(
        userId = UUID.randomUUID(),
        role = Role.COMPANY,
        creationDate = currentDate,
        expirationDate = mockAuthDomain.createTokenExpirationDate(currentDate)
    )

    private val mockUserRepo = MockRepos.mockUserRepo

    private fun generateDateToCompare(date: Date): Date =
        Calendar.getInstance().apply { time = date; set(Calendar.MILLISECOND, 0) }.time

    private fun createToken(): String {
        return mockJwtService.generateToken(
            tokenDetails.userId,
            tokenDetails.role,
            tokenDetails.expirationDate,
            tokenDetails.creationDate
        )
    }

    private fun createInvalidToken(): String {
        return mockJwtService.generateToken(
            tokenDetails.userId,
            tokenDetails.role,
            tokenDetails.creationDate,
            tokenDetails.expirationDate
        )
    }

    companion object {
        lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain

        @JvmStatic
        @BeforeAll
        fun setUp(@Autowired authDomain: AuthDomain, @Autowired secret: JWTSecret) {
            mockAuthDomain = spy(authDomain)
            mockJwtService = buildMockJwtService(secret)
        }
    }
}
