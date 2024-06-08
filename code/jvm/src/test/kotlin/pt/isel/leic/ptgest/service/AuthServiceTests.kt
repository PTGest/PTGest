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
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.service.MockServices.buildMockAuthServices
import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
import pt.isel.leic.ptgest.services.MailService
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import pt.isel.leic.ptgest.services.auth.JwtService
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@SpringBootTest
class AuthServiceTests {

    private val mockAuthRepo = MockRepos.mockAuthRepo
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

            `when`(mockAuthRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            mockAuthService.signUpCompany(name, email, password)
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

            `when`(mockAuthRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            mockAuthService.signUpIndependentTrainer(name, email, password, gender, phoneNumber)
        }

        @Test
        fun `create independent trainer Successfully with no phoneNumber`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockAuthRepo.createUser(name, email, passwordHash, role))
                .then { uuid }

            mockAuthService.signUpIndependentTrainer(name, email, password, gender, null)
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
            val currentDate = Date()
            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockUserRepo.getUserDetails(email))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            `when`(mockUserRepo.getUserDetails(uuid))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            val authenticationDetails = mockAuthService.login(currentDate, email, password)

            assertEquals(role, authenticationDetails.role)
            assertTrue(authenticationDetails.tokens.accessToken.token.isNotEmpty())
            assertTrue(authenticationDetails.tokens.refreshToken.token.isNotEmpty())
        }

        @Test
        fun `login with invalid password`() {
            val currentDate = Date()

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockUserRepo.getUserDetails(email))
                .then { UserDetails(uuid, name, email, passwordHash, role) }

            val exception = assertFailsWith<AuthError.UserAuthenticationError.InvalidPassword> {
                mockAuthService.login(currentDate, email, "invalidPassword")
            }

            assertEquals("Invalid password for user.", exception.message)
        }

        @Test
        fun `login with invalid email`() {
            val currentDate = Date()

            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            val exception = assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockAuthService.login(currentDate, email, password)
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

            `when`(mockAuthRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { TokenDetails(uuid, refreshTokenExpirationDate) }

            val tokens = mockAuthService.refreshToken(accessToken, refreshToken, currentDate)

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

            `when`(mockAuthRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { TokenDetails(uuid, refreshTokenExpirationDate) }

            assertFailsWith<AuthError.TokenError.TokenExpired> {
                mockAuthService.refreshToken(accessToken, refreshToken, currentDate)
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

            `when`(mockAuthRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { null }

            assertFailsWith<AuthError.TokenError.InvalidRefreshToken> {
                mockAuthService.refreshToken(accessToken, refreshToken, currentDate)
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

            `when`(mockAuthRepo.getRefreshTokenDetails(refreshTokenHash))
                .then { TokenDetails(UUID.randomUUID(), refreshTokenExpirationDate) }

            assertFailsWith<AuthError.TokenError.UserIdMismatch> {
                mockAuthService.refreshToken(accessToken, refreshToken, currentDate)
            }
        }
    }

    @AfterEach
    fun cleanUp() {
        reset(mockAuthRepo)
    }

    companion object {
        lateinit var mockAuthService: AuthService
        private lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain

        @JvmStatic
        @BeforeAll
        fun setUp(
            @Autowired authDomain: AuthDomain,
            @Autowired jwtSecret: JWTSecret,
            @Autowired mailService: MailService
        ) {
            mockAuthDomain = spy(authDomain)
            mockJwtService = buildMockJwtService(jwtSecret)
            mockAuthService = buildMockAuthServices(mockAuthDomain, mockJwtService, mailService)
        }
    }
}
