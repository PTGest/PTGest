package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.jdbi.auth.JdbiUserRepo
import java.util.*
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
}
