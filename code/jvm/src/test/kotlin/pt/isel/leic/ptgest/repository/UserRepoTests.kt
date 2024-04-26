package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.jdbi.auth.JdbiAuthRepo
import pt.isel.leic.ptgest.repository.jdbi.user.JdbiUserRepo
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class UserRepoTests {

    val jdbi = getDevJdbi()

    @Nested
    inner class UserCreationTests {

        @Test
        fun `create user Successfully`() {
            val userId = asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val authRepo = JdbiAuthRepo(handle)

                authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )
            }

            val user = asTransaction(jdbi) {
                val userRepo = JdbiUserRepo(it)
                val user = userRepo.getUserDetails(userId)
                it.cleanup()
                user
            }

            assertEquals(userId, user?.id)
        }

        @Test
        fun `create users with same email`() {
            val name = "UserTest"
            val email = "usertest@mail.com"
            val password = "passwordTest"
            val role = Role.COMPANY

            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)

                authRepo.createUser(name, email, password, role)
            }

            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(name, email, password, role)
                }
            }
        }

        @Test
        fun `create user with invalid name`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(
                        "UserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTestsUserTests",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }
        }

        @Test
        fun `create user with invalid email`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(
                        "UserTest",
                        "usertestmail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }
        }

        @Test
        fun `create user with empty name`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(
                        "",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }
        }

        @Test
        fun `create user with empty email`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(
                        "UserTest",
                        "",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }
        }

        @Test
        fun `create user with empty password`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "",
                        Role.COMPANY
                    )
                }
            }
        }
    }

    @Nested
    inner class CompanyCreationTests {

        @Test
        fun `create company Successfully`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)

                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                authRepo.createCompany(userId)
            }
        }

        @Test
        fun `create company with invalid user id`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    authRepo.createCompany(UUID.randomUUID())
                }
            }
        }
    }

    @Nested
    inner class IndependentTrainerCreationTests {

        @Test
        fun `create independent trainer Successfully without phone number`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.INDEPENDENT_TRAINER
                )

                authRepo.createTrainer(userId, Gender.MALE)
            }
        }

        @Test
        fun `create independent trainer Successfully with phone number`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.INDEPENDENT_TRAINER
                )

                authRepo.createTrainer(userId, Gender.MALE, "+351962005244")
            }
        }

        @Test
        fun `create independent trainer Unsuccessfully with wrong phone number`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    val userId = authRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.INDEPENDENT_TRAINER
                    )

                    authRepo.createTrainer(userId, Gender.MALE, "111")
                }
            }
        }

        @Test
        fun `create independent trainer Unsuccessfully with empty phone number`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    val userId = authRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.INDEPENDENT_TRAINER
                    )

                    authRepo.createTrainer(userId, Gender.MALE, "")
                }
            }
        }
    }

    @Nested
    inner class RefreshTokenCreationTests {

        @Test
        fun `create refresh token Successfully`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )
                val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)

                authRepo.createToken("tokenHash", userId, expirationDate)
                authRepo.createRefreshToken("tokenHash")
            }
        }

        @Test
        fun `create refresh token with invalid user id`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val authRepo = JdbiAuthRepo(handle)
                    val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
                    authRepo.createToken("tokenHash", UUID.randomUUID(), expirationDate)
                    authRepo.createRefreshToken("tokenHash")
                }
            }
        }

        @Test
        fun `create refresh token with invalid expiration date`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                assertFailsWith<UnableToExecuteStatementException> {
                    authRepo.createToken("tokenHash", userId, Date(0))
                    authRepo.createRefreshToken("tokenHash")
                }
            }
        }
    }

    @Nested
    inner class TokenDetailsTests {

        @Test
        fun `get refresh token details Successfully`() {
            val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
            val userId = asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                authRepo.createToken("tokenHash", userId, expirationDate)
                authRepo.createRefreshToken("tokenHash")

                userId
            }

            val refreshTokenDetails = asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                authRepo.getRefreshTokenDetails("tokenHash")
            }

            assertNotNull(refreshTokenDetails)
            assertEquals(userId, refreshTokenDetails.userId)
            assertEquals(expirationDate, refreshTokenDetails.expiration)
        }

        @Test
        fun `get refresh token details without refresh token in data base`() {
            val refreshTokenDetails = asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                authRepo.getRefreshTokenDetails("tokenHash")
            }

            assertNull(refreshTokenDetails)
        }
    }

    @Nested
    inner class RefreshTokenRemovalTests {

        @Test
        fun `get refresh token details Successfully`() {
            val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                val userId = authRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                authRepo.createToken("tokenHash", userId, expirationDate)
                authRepo.createRefreshToken("tokenHash")
            }
        }

        @Test
        fun `remove refresh token without refresh token in data base`() {
            asTransaction(jdbi) { handle ->
                val authRepo = JdbiAuthRepo(handle)
                authRepo.removeToken("tokenHash")
            }
        }
    }

    @Test
    fun `get user details by email Successfully`() {
        val name = "UserTest"
        val email = "usertest@mail.com"
        val password = "passwordTest"
        val role = Role.INDEPENDENT_TRAINER

        val user = asTransaction(jdbi) { handle ->
            val authRepo = JdbiAuthRepo(handle)
            val userRepo = JdbiUserRepo(handle)

            authRepo.createUser(
                name,
                email,
                password,
                role
            )

            userRepo.getUserDetails("usertest@mail.com")
        }

        assertNotNull(user)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(password, user.passwordHash)
        assertEquals(role, user.role)
    }

    @Test
    fun `get user details by email without user`() {
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            userRepo.getUserDetails("usertest@mail.com")
        }

        assertNull(user)
    }

    @Test
    fun `get user details by id Successfully`() {
        val name = "UserTest"
        val email = "usertest@mail.com"
        val password = "passwordTest"
        val role = Role.INDEPENDENT_TRAINER

        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            val authRepo = JdbiAuthRepo(handle)

            val userId = authRepo.createUser(
                name,
                email,
                password,
                role
            )

            userRepo.getUserDetails(userId)
        }

        assertNotNull(user)
        assertEquals(name, user.name)
        assertEquals(email, user.email)
        assertEquals(password, user.passwordHash)
        assertEquals(role, user.role)
    }

    @Test
    fun `get user details by id without user`() {
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            userRepo.getUserDetails(UUID.randomUUID())
        }

        assertNull(user)
    }

    @AfterEach
    fun cleanup() {
        asTransaction(jdbi) {
            it.cleanup()
        }
    }

    private fun createExpirationDate(currentDate: Date, units: Int, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(units, amount)
        return calendar.time
    }
}
