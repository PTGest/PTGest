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
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.service.MockServices.buildMockAuthServices
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import pt.isel.leic.ptgest.services.auth.JwtService
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

            val token = mockAuthService.login(email, password)

            assertTrue(token.token.isNotEmpty())
            assertTrue(token.expirationDate.after(Date()))
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

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo)
    }

    companion object {
        lateinit var mockAuthService: AuthService
        lateinit var mockAuthDomain: AuthDomain

        @JvmStatic
        @BeforeAll
        fun setUp(@Autowired authDomain: AuthDomain, @Autowired jwtService: JwtService) {
            mockAuthDomain = spy(authDomain)
            mockAuthService = buildMockAuthServices(jwtService, mockAuthDomain)
        }
    }
}
