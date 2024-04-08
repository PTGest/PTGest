package pt.isel.leic.ptgest.service

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
import pt.isel.leic.ptgest.domain.auth.model.RefreshTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.service.MockServices.buildMockAuthServices
import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import pt.isel.leic.ptgest.services.auth.JwtService
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@SpringBootTest
class AuthServiceTests {

    private val mockUserRepo = MockRepos.mockUserRepo

    @Nested
    inner class SignUpCompanyTests {
        private val uuid = UUID.randomUUID()
        private val name = "UserTest"
        private val email = "usertest@mai.com"
        private val password = "password1234"
        private val role = Role.COMPANY

        @Test
        fun `create company Successfully`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockUserRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            val result = mockAuthService.signUpCompany(name, email, password)

            assertEquals(uuid, result)
        }

        @Test
        fun `create company with existing email`() {
            val userDetails = UserDetails(uuid, name, email, password, role)

            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            val exception = assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                mockAuthService.signUpCompany(name, email, password)
            }

            assertEquals("User already exists.", exception.message)
        }
    }

    @Nested
    inner class SignUpIndependentTrainerTests {
        private val uuid = UUID.randomUUID()
        private val name = "UserTest"
        private val email = "usertest@mai.com"
        private val password = "password1234"
        private val gender = Gender.MALE
        private val phoneNumber = "+351911111111"
        private val role = Role.INDEPENDENT_TRAINER

        @Test
        fun `create independent trainer Successfully`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockUserRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            val result = mockAuthService.signUpIndependentTrainer(name, email, password, gender, phoneNumber)

            assertEquals(uuid, result)
        }

        @Test
        fun `create independent trainer Successfully with no phoneNumber`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockUserRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            val result = mockAuthService.signUpIndependentTrainer(name, email, password, gender, null)

            assertEquals(uuid, result)
        }

        @Test
        fun `create independent trainer with existing email`() {
            val userDetails = UserDetails(uuid, name, email, password, role)

            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            val exception = assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                mockAuthService.signUpIndependentTrainer(name, email, password, gender, phoneNumber)
            }

            assertEquals("User already exists.", exception.message)
        }
    }

    @Nested
    inner class LoginTests {
        private val uuid = UUID.randomUUID()
        private val name = "UserTest"
        private val email = "usertest@mai.com"
        private val password = "password1234"
        private val role = Role.COMPANY

        @Test
        fun `login Successfully`() {
            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockUserRepo.getUserDetails(email))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            val tokens = mockAuthService.login(email, password)

            assertTrue(tokens.accessToken.token.isNotEmpty())
            assertTrue(tokens.refreshToken.token.isNotEmpty())
        }

        @Test
        fun `login with invalid password`() {
            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockUserRepo.getUserDetails(email))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            val exception = assertFailsWith<AuthError.UserAuthenticationError.InvalidPassword> {
                mockAuthService.login(email, "invalidPassword")
            }

            assertEquals("Invalid password for user.", exception.message)
        }

        @Test
        fun `login with invalid email`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val exception = assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockAuthService.login(email, password)
            }

            assertEquals("User not found.", exception.message)
        }
    }

    @Nested
    inner class RefreshTokenTests {
        private val uuid = UUID.randomUUID()
        private val name = "UserTest"
        private val email = "usertest@mai.com"
        private val password = "password1234"
        private val role = Role.COMPANY

        @Test
        fun `refresh token Successfully`() {
            val currentDate = Date()

            val accessTokenExpirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate)

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, password, role) }

            val accessToken = mockJwtService.generateToken(
                uuid,
                role,
                accessTokenExpirationDate,
                currentDate
            )

            val refreshToken = mockAuthDomain.generateTokenValue()
            val refreshTokenHash = mockAuthDomain.hashToken(refreshToken)
            val refreshTokenExpirationDate = mockAuthDomain.createRefreshTokenExpirationDate(currentDate)

            `when`(mockAuthDomain.hashToken(refreshToken))
                .then { refreshTokenHash }

            `when`(mockUserRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { RefreshTokenDetails(uuid, refreshTokenExpirationDate) }

            val tokens = mockAuthService.refreshToken(accessToken, refreshToken)

            assertTrue(tokens.accessToken.token.isNotEmpty())
            assertTrue(tokens.refreshToken.token.isNotEmpty())
        }

        @Test
        fun `refresh token with expired refresh token`() {
            val currentDate = Date()

            val accessTokenExpirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate)

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, password, role) }

            val accessToken = mockJwtService.generateToken(
                uuid,
                role,
                accessTokenExpirationDate,
                currentDate
            )

            val refreshToken = mockAuthDomain.generateTokenValue()
            val refreshTokenHash = mockAuthDomain.hashToken(refreshToken)
            val refreshTokenExpirationDate = Calendar.getInstance().apply {
                time = currentDate
                add(Calendar.HOUR, -1)
            }.time

            `when`(mockAuthDomain.hashToken(refreshToken))
                .then { refreshTokenHash }

            `when`(mockUserRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { RefreshTokenDetails(uuid, refreshTokenExpirationDate) }

            assertFailsWith<AuthError.TokenError.TokenExpired> {
                mockAuthService.refreshToken(accessToken, refreshToken)
            }
        }

        @Test
        fun `refresh token with invalid refresh token`() {
            val currentDate = Date()

            val accessTokenExpirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate)

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, password, role) }

            val accessToken = mockJwtService.generateToken(
                uuid,
                role,
                accessTokenExpirationDate,
                currentDate
            )

            val refreshToken = mockAuthDomain.generateTokenValue()
            val refreshTokenHash = mockAuthDomain.hashToken(refreshToken)

            `when`(mockAuthDomain.hashToken(refreshToken))
                .then { refreshTokenHash }

            `when`(mockUserRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { null }

            assertFailsWith<AuthError.TokenError.InvalidRefreshToken> {
                mockAuthService.refreshToken(accessToken, refreshToken)
            }
        }

        @Test
        fun `refresh token with userId mismatch`() {
            val currentDate = Date()

            val accessTokenExpirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate)

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, password, role) }

            val accessToken = mockJwtService.generateToken(
                uuid,
                role,
                accessTokenExpirationDate,
                currentDate
            )

            val refreshToken = mockAuthDomain.generateTokenValue()
            val refreshTokenHash = mockAuthDomain.hashToken(refreshToken)
            val refreshTokenExpirationDate = mockAuthDomain.createRefreshTokenExpirationDate(currentDate)

            `when`(mockAuthDomain.hashToken(refreshToken))
                .then { refreshTokenHash }

            `when`(mockUserRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { RefreshTokenDetails(UUID.randomUUID(), refreshTokenExpirationDate) }

            assertFailsWith<AuthError.TokenError.UserIdMismatch> {
                mockAuthService.refreshToken(accessToken, refreshToken)
            }
        }
    }

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo)
    }

    companion object {
        lateinit var mockAuthService: AuthService
        private lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain

        @JvmStatic
        @BeforeAll
        fun setUp(@Autowired authDomain: AuthDomain, @Autowired jwtSecret: JWTSecret) {
            mockAuthDomain = spy(authDomain)
            mockJwtService = buildMockJwtService(jwtSecret)
            mockAuthService = buildMockAuthServices(mockJwtService, mockAuthDomain)
        }
    }
}
