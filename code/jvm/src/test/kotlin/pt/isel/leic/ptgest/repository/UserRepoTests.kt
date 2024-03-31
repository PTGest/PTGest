package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.PtgestApplication
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.jdbi.auth.JdbiUserRepo
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(classes = [PtgestApplication::class])
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

            assertThrows<UnableToExecuteStatementException> {
                asTransaction(jdbi) { handle ->
                    val userRepo = JdbiUserRepo(handle)
                    userRepo.createUser(name, email, password, role)
                }
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create user with invalid name`() {
            assertThrows<UnableToExecuteStatementException> {
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
            assertThrows<UnableToExecuteStatementException> {
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
            assertThrows<UnableToExecuteStatementException> {
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
            assertThrows<UnableToExecuteStatementException> {
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
            assertThrows<UnableToExecuteStatementException> {
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
            assertDoesNotThrow {
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
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create company with invalid user id`() {
            assertThrows<UnableToExecuteStatementException> {
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
            assertDoesNotThrow {
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
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create independent trainer Successfully with contact`() {
            assertDoesNotThrow {
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
            }

            asTransaction(jdbi) { it.cleanup() }
        }

        @Test
        fun `create independent trainer Unsuccessfully with wrong contact`() {
            assertThrows<UnableToExecuteStatementException> {
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
            assertThrows<UnableToExecuteStatementException> {
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
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            userRepo.createUser(
                "UserTest",
                "usertest@mail.com",
                "passwordTest",
                Role.INDEPENDENT_TRAINER
            )

            userRepo.getUserDetails("usertest@mail.com")
        }

        asTransaction(jdbi) { it.cleanup() }

        assertTrue { user != null }
    }

    @Test
    fun `get user details by email without user`() {
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            userRepo.getUserDetails("usertest@mail.com")
        }

        assertTrue { user == null }
    }

    @Test
    fun `get user details by id Successfully`() {
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            val userId = userRepo.createUser(
                "UserTest",
                "usertest@mail.com",
                "passwordTest",
                Role.INDEPENDENT_TRAINER
            )

            userRepo.getUserDetails(userId)
        }

        asTransaction(jdbi) { it.cleanup() }

        assertTrue { user != null }
    }

    @Test
    fun `get user details by id without user`() {
        val user = asTransaction(jdbi) { handle ->
            val userRepo = JdbiUserRepo(handle)
            userRepo.getUserDetails(UUID.randomUUID())
        }

        assertTrue { user == null }
    }
}
