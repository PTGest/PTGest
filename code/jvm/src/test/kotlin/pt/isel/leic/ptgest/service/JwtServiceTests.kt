package pt.isel.leic.ptgest.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.reset
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.Sha256TokenEncoder
import pt.isel.leic.ptgest.domain.auth.model.AuthTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.service.AuthServiceTests.Companion
import pt.isel.leic.ptgest.service.MockRepos.mockAuthRepo
import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
import pt.isel.leic.ptgest.services.JwtService
import pt.isel.leic.ptgest.services.errors.AuthError
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JwtServiceTests {

    private val mockUserRepo = MockRepos.mockUserRepo

    @Nested
    inner class GenerateTokenTests {

        @Test
        fun `should generate token successfully`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { authTokenDetails.version }

            val token = createToken()
            val tokenDetails = decryptToken(token)

            assert(token.isNotEmpty())
            assertEquals(authTokenDetails.userId, tokenDetails.userId)
            assertEquals(authTokenDetails.role, tokenDetails.role)
            assertEquals(
                generateDateToCompare(authTokenDetails.expirationDate),
                generateDateToCompare(tokenDetails.expirationDate)
            )
            assertEquals(authTokenDetails.version, tokenDetails.version)
        }

        @Test
        fun `should fail to generate token when user details are not found`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> { createToken() }
        }

        @Test
        fun `should generate token without token version`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { null }

            val token = createToken()
            val tokenDetails = decryptToken(token)

            assert(token.isNotEmpty())
            assertEquals(authTokenDetails.userId, tokenDetails.userId)
            assertEquals(authTokenDetails.role, tokenDetails.role)
            assertEquals(
                generateDateToCompare(authTokenDetails.expirationDate),
                generateDateToCompare(tokenDetails.expirationDate)
            )
            assertEquals(authTokenDetails.version, tokenDetails.version)
        }

        @Test
        fun `should fail to generate token with expiration date before current date`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            assertFailsWith<IllegalArgumentException> {
                createInvalidToken()
            }
        }

        @Test
        fun `should fail to generate token with expiration date same as current date`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            assertFailsWith<IllegalArgumentException> {
                mockJwtService.generateToken(
                    authTokenDetails.userId,
                    authTokenDetails.role,
                    currentDate,
                    currentDate
                )
            }
        }

        @Test
        fun `should fail to generate token with invalid user role`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails.copy(role = Role.INDEPENDENT_TRAINER) }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidUserRoleException> {
                createToken()
            }
        }

        private fun decryptToken(token: String): AuthTokenDetails {
            val claims = Jwts.parser().setSigningKey(testSecret.value).parseClaimsJws(token).body

            val expirationDate = claims.expiration
            val version = claims["version"] as Int

            return AuthTokenDetails(
                userId = UUID.fromString(claims.id),
                role = Role.valueOf(claims.subject),
                expirationDate = Date(expirationDate.time),
                version = version
            )
        }

        private fun createToken(): String {
            return mockJwtService.generateToken(
                authTokenDetails.userId,
                authTokenDetails.role,
                authTokenDetails.expirationDate,
                currentDate
            )
        }
    }

    @Nested
    inner class ExtractTokenTests {

        @Test
        fun `should extract token successfully`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { authTokenDetails.version }

            val token = createToken(authTokenDetails)
            val extractedToken = mockJwtService.extractToken(token)

            assertEquals(authTokenDetails.userId, extractedToken.userId)
            assertEquals(authTokenDetails.role, extractedToken.role)
            assertEquals(
                generateDateToCompare(authTokenDetails.expirationDate),
                generateDateToCompare(extractedToken.expirationDate)
            )
            assertEquals(authTokenDetails.version, extractedToken.version)
        }

        @Test
        fun `should fail to extract token with mismatched role`() {
            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { authTokenDetails.version }

            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails.copy(role = Role.INDEPENDENT_TRAINER) }

            val token = createToken(authTokenDetails)

            assertFailsWith<AuthError.UserAuthenticationError.InvalidUserRoleException> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `should fail to extract token when token is expired`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { authTokenDetails.version }

            val expirationDate = Calendar.getInstance()
                .apply { time = currentDate; add(Calendar.MONTH, -1) }.time

            val token = createToken(authTokenDetails.copy(expirationDate = expirationDate))

            assertFailsWith<ExpiredJwtException> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `should fail to extract token when user details are not found`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { null }

            val token = createToken(authTokenDetails)

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `should fail to extract token with invalid token version`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { authTokenDetails.version + 1 }

            val token = createToken(authTokenDetails)

            assertFailsWith<AuthError.UserAuthenticationError.InvalidTokenVersion> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `should fail to extract token with null token version`() {
            `when`(mockUserRepo.getUserDetails(authTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(authTokenDetails.userId))
                .then { null }

            val token = createToken(authTokenDetails)

            assertFailsWith<AuthError.UserAuthenticationError.InvalidTokenVersion> {
                mockJwtService.extractToken(token)
            }
        }

        private fun createToken(authTokenDetails: AuthTokenDetails): String {
            val claims = HashMap<String, Any>()
            claims["version"] = authTokenDetails.version

            return Jwts.builder().setClaims(claims)
                .setId(authTokenDetails.userId.toString())
                .setSubject(authTokenDetails.role.name)
                .setExpiration(authTokenDetails.expirationDate)
                .signWith(SignatureAlgorithm.HS256, testSecret.value).compact()
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
        role = Role.COMPANY,
        active = true
    )

    private val authTokenDetails = AuthTokenDetails(
        userId = UUID.randomUUID(),
        role = Role.COMPANY,
        expirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate),
        version = 1
    )

    private fun generateDateToCompare(date: Date): Date =
        Calendar.getInstance().apply { time = date; set(Calendar.MILLISECOND, 0) }.time

    private fun createInvalidToken(): String {
        return mockJwtService.generateToken(
            authTokenDetails.userId,
            authTokenDetails.role,
            Calendar.getInstance().apply { time = currentDate; add(Calendar.MONTH, -1) }.time,
            currentDate
        )
    }

    companion object {
        lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain
        lateinit var testSecret: JWTSecret

        @JvmStatic
        @BeforeAll
        fun setUp() {

            val passwordEncoder = BCryptPasswordEncoder()
            val tokenEncoder = Sha256TokenEncoder()

            mockAuthDomain = spy(AuthDomain(passwordEncoder, tokenEncoder))
            testSecret = JWTSecret("testSecret")
            mockJwtService = buildMockJwtService(testSecret)
        }
    }
}
