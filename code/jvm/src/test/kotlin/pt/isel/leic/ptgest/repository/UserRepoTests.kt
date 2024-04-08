package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.jdbi.auth.JdbiUserRepo
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
                userRepo.createUser(
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
                val userRepo = JdbiUserRepo(handle)
                userRepo.createUser(name, email, password, role)
            }

            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(name, email, password, role)
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create user with invalid name`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(
                        "UserTestsUserTestsUserTestsUserTests",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create user with invalid email`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(
                        "UserTest",
                        "usertestmail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create user with empty name`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(
                        "",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.COMPANY
                    )
                }

                asTransaction(jdbi) { it.cleanup() }
            }
        }

        @Test
        fun `create user with empty email`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(
                        "UserTest",
                        "",
                        "passwordTest",
                        Role.COMPANY
                    )
                }

                asTransaction(jdbi) { it.cleanup() }
            }
        }

        @Test
        fun `create user with empty password`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "",
                        Role.COMPANY
                    )
                }

                asTransaction(jdbi) { it.cleanup() }
            }
        }
    }

    @Nested
    inner class CompanyCreationTests {

        @Test
        fun `create company Successfully`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)

                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                userRepo.createCompany(userId)
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create company with invalid user id`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createCompany(UUID.randomUUID())
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }
    }

    @Nested
    inner class IndependentTrainerCreationTests {

        @Test
        fun `create independent trainer Successfully without contact`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.INDEPENDENT_TRAINER
                )

                userRepo.createIndependentTrainer(userId, Gender.MALE)
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create independent trainer Successfully with contact`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.INDEPENDENT_TRAINER
                )

                userRepo.createIndependentTrainer(userId, Gender.MALE, "+351962005244")
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create independent trainer Unsuccessfully with wrong contact`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    val userId = userRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.INDEPENDENT_TRAINER
                    )

                    userRepo.createIndependentTrainer(userId, Gender.MALE, "111")
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create independent trainer Unsuccessfully with empty contact`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    val userId = userRepo.createUser(
                        "UserTest",
                        "usertest@mail.com",
                        "passwordTest",
                        Role.INDEPENDENT_TRAINER
                    )

                    userRepo.createIndependentTrainer(userId, Gender.MALE, "")
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }
    }

    @Nested
    inner class RefreshTokenCreationTests {

        @Test
        fun `create refresh token Successfully`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )
                val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
                userRepo.createRefreshToken(userId, "tokenHash", expirationDate)
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create refresh token with invalid user id`() {
            assertFailsWith<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
                    userRepo.createRefreshToken(UUID.randomUUID(), "tokenHash", expirationDate)
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create refresh token with invalid expiration date`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                assertFailsWith<UnableToExecuteStatementException> {
                    userRepo.createRefreshToken(userId, "tokenHash", Date(0))
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }
    }

    @Nested
    inner class RefreshTokenDetailsTests {

        @Test
        fun `get refresh token details Successfully`() {
            val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
            val userId = asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                userRepo.createRefreshToken(userId, "tokenHash", expirationDate)

                userId
            }

            val refreshTokenDetails = asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                userRepo.getRefreshTokenDetails("tokenHash")
            }

            assertNotNull(refreshTokenDetails)
            assertEquals(userId, refreshTokenDetails.userId)
            assertEquals(expirationDate, refreshTokenDetails.expiration)

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `get refresh token details without refresh token in data base`() {
            val refreshTokenDetails = asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                userRepo.getRefreshTokenDetails("tokenHash")
            }

            assertNull(refreshTokenDetails)

            asTransaction(jdbi) { it.cleanup() }
        }
    }

    @Nested
    inner class RefreshTokenRemovalTests {

        @Test
        fun `get refresh token details Successfully`() {
            val expirationDate = createExpirationDate(Date(), Calendar.DAY_OF_MONTH, 1)
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                val userId = userRepo.createUser(
                    "UserTest",
                    "usertest@mail.com",
                    "passwordTest",
                    Role.COMPANY
                )

                userRepo.createRefreshToken(userId, "tokenHash", expirationDate)
            }

            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                userRepo.removeRefreshToken("tokenHash")
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `remove refresh token without refresh token in data base`() {
            asTransaction(jdbi) { handle ->
                val userRepo = JdbiUserRepo(handle)
                userRepo.removeRefreshToken("tokenHash")
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
            val userRepo = JdbiUserRepo(handle)
            userRepo.createUser(
                name,
                email,
                password,
                role
            )

            userRepo.getUserDetails("usertest@mail.com")
        }

        asTransaction(jdbi) { it.cleanup() }

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
            val userId = userRepo.createUser(
                name,
                email,
                password,
                role
            )

            userRepo.getUserDetails(userId)
        }

        asTransaction(jdbi) { it.cleanup() }

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

    private fun createExpirationDate(currentDate: Date, units: Int, amount: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(units, amount)
        return calendar.time
    }
}
