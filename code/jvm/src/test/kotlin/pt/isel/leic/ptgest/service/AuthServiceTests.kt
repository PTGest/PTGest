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
 import pt.isel.leic.ptgest.service.MockServices.buildMockAuthService
 import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
 import pt.isel.leic.ptgest.services.errors.AuthError
 import pt.isel.leic.ptgest.services.AuthService
 import pt.isel.leic.ptgest.services.JwtService
 import java.util.*
 import kotlin.test.Test
 import kotlin.test.assertFailsWith

 @SpringBootTest
 class AuthServiceTests {

    private val mockAuthRepo = MockRepos.mockAuthRepo
    private val mockUserRepo = MockRepos.mockUserRepo

     @Nested
     inner class SignUpCompanyTests {
            @Test
            fun `should sign up company successfully`() {
                `when`(mockUserRepo.userExists(email))
                    .then { false }

                val passwordHash = mockAuthDomain.hashPassword(password)

                `when`(mockAuthDomain.hashPassword(password))
                    .then { passwordHash }

                `when`(mockAuthRepo.createUser(username, email, passwordHash, Role.COMPANY))
                    .then { userId }

                mockAuthService.signUpCompany(username, email, password)
            }

            @Test
            fun `should fail to sign up company when user already exists`() {
                `when`(mockUserRepo.userExists(email))
                    .then { true }

                assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                    mockAuthService.signUpCompany(username, email, password)
                }
            }
     }

    @Nested
    inner class SignUpIndependentTrainerTests {
        @Test
        fun `should sign up independent trainer successfully`() {
            `when`(mockUserRepo.userExists(email))
                .then { false }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockAuthRepo.createUser(username, email, passwordHash, Role.INDEPENDENT_TRAINER))
                .then { userId }

            mockAuthService.signUpIndependentTrainer(username, email, password, gender, phoneNumber)
        }

        @Test
        fun `should fail to sign up independent trainer when user already exists`() {
            `when`(mockUserRepo.userExists(email))
                .then { true }

            assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                mockAuthService.signUpIndependentTrainer(username, email, password, gender, phoneNumber)
            }
        }
    }

    @Nested
    inner class SignUpHiredTrainerTests {
        private val userDetails = UserDetails(
            userId,
            username,
            email,
            password,
            Role.HIRED_TRAINER,
            true
        )

        @Test
        fun `should sign up hired trainer successfully`() {
            `when`(mockUserRepo.userExists(email))
                .then { false }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockAuthRepo.createUser(username, email, passwordHash, Role.HIRED_TRAINER))
                .then { userId }

            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            mockAuthService.signUpHiredTrainer(userId, username, email, gender, 10, phoneNumber)
        }

        @Test
        fun `should fail to sign up hired trainer when user already exists`() {
            `when`(mockUserRepo.userExists(email))
                .then { true }

            assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                mockAuthService.signUpHiredTrainer(userId, username, email, gender, 10, phoneNumber)
            }
        }

        @Test
        fun `should fail to sign up hired trainer when the capacity is below 1`() {
            assertFailsWith<IllegalArgumentException> {
                mockAuthService.signUpHiredTrainer(userId, username, email, gender, 0, phoneNumber)
            }
        }
    }

    @Nested
    inner class SignUpTraineeTests {
        private val userDetails = UserDetails(
            userId,
            username,
            email,
            password,
            Role.TRAINEE,
            true
        )

        private val validBirthdate = Calendar.getInstance().apply {
            set(2000, 1, 1)
        }.time

        @Test
        fun `should sign up trainee from company successfully`() {
            `when`(mockUserRepo.userExists(email))
                .then { false }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockAuthRepo.createUser(username, email, passwordHash, Role.TRAINEE))
                .then { userId }

            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            mockAuthService.signUpTrainee(userId, Role.COMPANY,
                username, email, validBirthdate, gender,
                phoneNumber)
        }

        @Test
        fun `should sign up trainee from independent trainer successfully`() {
            `when`(mockUserRepo.userExists(email))
                .then { false }

            val passwordHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { passwordHash }

            `when`(mockAuthRepo.createUser(username, email, passwordHash, Role.TRAINEE))
                .then { userId }

            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            mockAuthService.signUpTrainee(userId,
                Role.INDEPENDENT_TRAINER, username, email, validBirthdate, gender, phoneNumber)
        }

        @Test
        fun `should fail to sign up trainee when the trainee birthdate is invalid`() {
            assertFailsWith<IllegalArgumentException> {
                mockAuthService.signUpTrainee(userId,
                    Role.INDEPENDENT_TRAINER, username, email, Date(), gender, phoneNumber)
            }
        }

        @Test
        fun `should fail to sign up trainee when user already exists`() {
            `when`(mockUserRepo.userExists(email))
                .then { true }

            assertFailsWith<AuthError.UserRegistrationError.UserAlreadyExists> {
                mockAuthService.signUpTrainee(
                    userId,
                    Role.INDEPENDENT_TRAINER,
                    username,
                    email,
                    validBirthdate,
                    gender,
                    phoneNumber
                )
            }
        }

        @Test
        fun `should fail to sign up trainee when the user is not a company nor an independent trainer`() {
            assertFailsWith<AuthError.UserAuthenticationError.UnauthorizedRole> {
                mockAuthService.signUpTrainee(
                    userId,
                    Role.TRAINEE,
                    username,
                    email,
                    validBirthdate,
                    gender,
                    phoneNumber
                )
            }
        }
    }

    @Nested
    inner class ForgetPasswordTests {
        private val userDetails = UserDetails(
            userId,
            username,
            email,
            password,
            Role.TRAINEE,
            true
        )

        @Test
        fun `should send password reset email successfully`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            val tokenHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { tokenHash }

            mockAuthService.forgetPassword(email)
        }

        @Test
        fun `should send password set email successfully`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { userDetails }

            val tokenHash = mockAuthDomain.hashPassword(password)

            `when`(mockAuthDomain.hashPassword(password))
                .then { tokenHash }

            mockAuthService.forgetPassword(email, true)
        }

        @Test
        fun `should fail to send password reset email when user does not exist`() {
            `when`(mockUserRepo.getUserDetails(email))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockAuthService.forgetPassword(email)
            }
        }
    }

    @Nested
    inner class ValidatePasswordResetRequestTests {
        private val requestToken = "requestToken"
        private val validExpirationDate = Calendar.getInstance().apply {
            set(2025, 1, 1)
        }.time
        private val invalidExpirationDate = Calendar.getInstance().apply {
            set(2019, 1, 1)
        }.time

        @Test
        fun `should validate password reset request successfully`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, validExpirationDate) }

            mockAuthService.validatePasswordResetRequest(requestToken)
        }

        @Test
        fun `should fail to validate password reset request when the token is empty`() {
            assertFailsWith<IllegalArgumentException> {
                mockAuthService.validatePasswordResetRequest("")
            }
        }

        @Test
        fun `should fail to validate password reset request when the token is invalid`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidPasswordResetRequest> {
                mockAuthService.validatePasswordResetRequest(requestToken)
            }
        }

        @Test
        fun `should fail to validate password reset request when the token is expired`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, invalidExpirationDate) }

            assertFailsWith<AuthError.UserAuthenticationError.PasswordRequestExpired> {
                mockAuthService.validatePasswordResetRequest(requestToken)
            }
        }
    }

    @Nested
    inner class ResetPasswordTests {
        private val requestToken = "requestToken"
        private val validExpirationDate = Calendar.getInstance().apply {
            set(2025, 1, 1)
        }.time
        private val invalidExpirationDate = Calendar.getInstance().apply {
            set(2019, 1, 1)
        }.time

        @Test
        fun `should reset password successfully with no token version`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, validExpirationDate) }

            `when`(mockUserRepo.getUserDetails(userId))
                .then { UserDetails(userId, username, email, password, Role.TRAINEE, true) }

            `when`(mockAuthRepo.getTokenVersion(userId))
                .then { null }

            mockAuthService.resetPassword(requestToken, password)
        }

        @Test
        fun `should reset password successfully with token version`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, validExpirationDate) }

            `when`(mockUserRepo.getUserDetails(userId))
                .then { UserDetails(userId, username, email, password, Role.TRAINEE, true) }

            `when`(mockAuthRepo.getTokenVersion(userId))
                .then { 1 }

            mockAuthService.resetPassword(requestToken, password)
        }

        @Test
        fun `should fail to reset password when the token is empty`() {
            assertFailsWith<IllegalArgumentException> {
                mockAuthService.resetPassword("", password)
            }
        }

        @Test
        fun `should fail to reset password when the request token does not exist`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidPasswordResetRequest> {
                mockAuthService.resetPassword(requestToken, password)
            }
        }

        @Test
        fun `should fail to reset password when the user does not exist`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, validExpirationDate) }

            `when`(mockUserRepo.getUserDetails(userId))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockAuthService.resetPassword(requestToken, password)
            }
        }

        @Test
        fun `should fail to reset password when the request token is expired`() {
            val tokenHash = mockAuthDomain.hashToken(requestToken)

            `when`(mockAuthDomain.hashToken(requestToken))
                .then { tokenHash }

            `when`(mockAuthRepo.getPasswordResetRequest(tokenHash))
                .then { TokenDetails(userId, invalidExpirationDate) }

            `when`(mockUserRepo.getUserDetails(userId))
                .then { UserDetails(userId, username, email, password, Role.TRAINEE, true) }

            assertFailsWith<AuthError.UserAuthenticationError.PasswordRequestExpired> {
                mockAuthService.resetPassword(requestToken, password)
            }
        }
    }

    @Nested
    inner class LoginTests {}

    @Nested
    inner class RefreshTokenTests {}

    @Nested
    inner class ChangePasswordTests {}

     private val username = "username"
     private val password = "password"
     private val email = "email"
     private val userId = UUID.randomUUID()
     private val gender = Gender.UNDEFINED
     private val phoneNumber = "phoneNumber"


    @AfterEach
    fun cleanUp() {
        reset(mockAuthRepo, mockUserRepo)
    }

    companion object {
        lateinit var mockAuthService: AuthService
        lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain

        @JvmStatic
        @BeforeAll
        fun setUp(
            @Autowired authDomain: AuthDomain,
            @Autowired jwtSecret: JWTSecret,
        ) {
            mockAuthDomain = spy(authDomain)
            mockJwtService = buildMockJwtService(jwtSecret)
            mockAuthService = buildMockAuthService(mockAuthDomain, mockJwtService)
        }
    }
 }
