package pt.isel.leic.ptgest.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.trainee.model.TraineeDetails
import pt.isel.leic.ptgest.domain.trainer.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.service.MockServices.buildMockUserService
import pt.isel.leic.ptgest.services.UserService
import pt.isel.leic.ptgest.services.errors.UserError
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTests {

    private val mockUserRepo = MockRepos.mockUserRepo
    private val mockTraineeRepo = MockRepos.mockTraineeRepo
    private val mockTrainerRepo = MockRepos.mockTrainerRepo

    @Nested
    inner class GetUserDetailsTests {
        @Test
        fun `should return user details`() {
            `when`(mockUserRepo.getUserDetails(userDetails.id))
                .then { userDetails }

            val userDetails = mockUserService.getUserDetails(userDetails.id)

            assertEquals(this@UserServiceTests.userDetails, userDetails)
        }

        @Test
        fun `should throw error when user does not exist`() {
            `when`(mockUserRepo.getUserDetails(userDetails.id))
                .then { null }

            assertThrows<UserError.UserNotFound> {
                mockUserService.getUserDetails(userDetails.id)
            }
        }
    }

    @Nested
    inner class GetTraineeDetailsTests {
        @Test
        fun `should return trainee details`() {
            `when`(mockTraineeRepo.getTraineeDetails(userDetails.id))
                .then { traineeDetails }

            val traineeDetails = mockUserService.getTraineeDetails(userDetails.id)

            assertEquals(this@UserServiceTests.traineeDetails, traineeDetails)
        }

        @Test
        fun `should throw error when trainee does not exist`() {
            `when`(mockTraineeRepo.getTraineeDetails(userDetails.id))
                .then { null }

            assertThrows<UserError.UserNotFound> {
                mockUserService.getTraineeDetails(userDetails.id)
            }
        }
    }

    @Nested
    inner class GetTrainerDetailsTests {
        @Test
        fun `should return trainer details`() {
            `when`(mockTrainerRepo.getTrainerDetails(userDetails.id))
                .then { trainerDetails }

            val trainerDetails = mockUserService.getTrainerDetails(userDetails.id)

            assertEquals(this@UserServiceTests.trainerDetails, trainerDetails)
        }

        @Test
        fun `should throw error when trainer does not exist`() {
            `when`(mockTrainerRepo.getTrainerDetails(userDetails.id))
                .then { null }

            assertThrows<UserError.UserNotFound> {
                mockUserService.getTrainerDetails(userDetails.id)
            }
        }
    }

    private val userDetails = UserDetails(
        UUID.randomUUID(),
        "traineeUsername",
        "traineeEmail",
        "traineePasswordHash",
        Role.TRAINEE,
        true
    )

    private val traineeDetails = TraineeDetails(
        Gender.UNDEFINED,
        Date(),
        "traineePhoneNumber"
    )

    private val trainerDetails = TrainerDetails(
        Gender.UNDEFINED,
        "trainerPhoneNumber"
    )

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo, mockTraineeRepo, mockTrainerRepo)
    }

    companion object {
        lateinit var mockUserService: UserService

        @JvmStatic
        @BeforeAll
        fun setUp() {
            mockUserService = buildMockUserService()
        }
    }
}
