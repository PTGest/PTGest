package pt.isel.leic.ptgest.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.service.MockServices.buildMockCompanyService
import pt.isel.leic.ptgest.services.CompanyService
import pt.isel.leic.ptgest.services.errors.CompanyError
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import pt.isel.leic.ptgest.services.errors.UserError
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

@SpringBootTest
class CompanyServiceTests {

    private val mockUserRepo = MockRepos.mockUserRepo
    private val mockCompanyRepo = MockRepos.mockCompanyRepo
    private val mockTraineeRepo = MockRepos.mockTraineeRepo
    private val mockSessionRepo = MockRepos.mockSessionRepo
    private val mockExerciseRepo = MockRepos.mockExerciseRepo

    @Nested
    inner class GetCompanyTrainersTests {
        @Test
        fun `should return trainers successfully`() {
            `when`(
                mockCompanyRepo.getTrainers(
                    companyId = companyId,
                    skip = 0,
                    limit = 10,
                    gender = null,
                    availability = Order.ASC,
                    name = null,
                    excludeTraineeTrainer = null
                )
            )
                .then { emptyList<Trainer>() }

            `when`(
                mockCompanyRepo.getTotalTrainers(
                    companyId = companyId,
                    gender = null,
                    name = null,
                    excludeTraineeTrainer = null
                )
            )
                .then { 0 }

            mockCompanyService.getCompanyTrainers(
                companyId,
                null,
                Order.ASC,
                null,
                null,
                0,
                null
            )
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainers(
                    companyId,
                    null,
                    Order.ASC,
                    null,
                    null,
                    -1,
                    null
                )
            }
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainers(
                    companyId,
                    null,
                    Order.ASC,
                    null,
                    null,
                    0,
                    -1
                )
            }
        }

        @Test
        fun `should fail if name is blank`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainers(
                    companyId,
                    null,
                    Order.ASC,
                    "",
                    null,
                    0,
                    null
                )
            }
        }
    }

    @Nested
    inner class GetCompanyTraineesTests {
        @Test
        fun `should return trainees successfully`() {
            `when`(
                mockCompanyRepo.getTrainees(
                    companyId = companyId,
                    skip = 0,
                    limit = null,
                    gender = null,
                    name = null
                )
            )
                .then { emptyList<Trainer>() }

            `when`(
                mockCompanyRepo.getTotalTrainees(
                    companyId = companyId,
                    gender = null,
                    name = null
                )
            )
                .then { 0 }

            mockCompanyService.getCompanyTrainees(
                companyId,
                null,
                null,
                null,
                null
            )
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainees(
                    companyId,
                    null,
                    null,
                    -1,
                    null
                )
            }
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainees(
                    companyId,
                    null,
                    null,
                    null,
                    -1
                )
            }
        }

        @Test
        fun `should fail if name is blank`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getCompanyTrainees(
                    companyId,
                    null,
                    "",
                    null,
                    null
                )
            }
        }
    }

    @Nested
    inner class AssignTrainerToTraineeTests {
        private val trainer = Trainer(
            UUID.randomUUID(),
            "trainerName",
            Gender.UNDEFINED,
            0,
            10
        )

        private val traineeId = UUID.randomUUID()

        @Test
        fun `should assign trainer to trainee successfully`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { trainer }

            `when`(mockCompanyRepo.isTraineeFromCompany(traineeId, companyId)).then { true }

            mockCompanyService.assignTrainerToTrainee(
                companyId,
                trainer.id,
                traineeId
            )
        }

        @Test
        fun `should fail if trainer not found`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { null }

            assertFailsWith<CompanyError.TrainerNotFound> {
                mockCompanyService.assignTrainerToTrainee(
                    companyId,
                    trainer.id,
                    traineeId
                )
            }
        }

        @Test
        fun `should fail if trainee is not from company`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { trainer }

            `when`(mockCompanyRepo.isTraineeFromCompany(traineeId, companyId)).then { false }

            assertFailsWith<CompanyError.TraineeNotFromCompany> {
                mockCompanyService.assignTrainerToTrainee(
                    companyId,
                    trainer.id,
                    traineeId
                )
            }
        }

        @Test
        fun `should fail if trainer capacity reached`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { trainer.copy(assignedTrainees = 10) }

            `when`(mockCompanyRepo.isTraineeFromCompany(traineeId, companyId)).then { true }

            assertFailsWith<CompanyError.TrainerCapacityReached> {
                mockCompanyService.assignTrainerToTrainee(
                    companyId,
                    trainer.id,
                    traineeId
                )
            }
        }
    }

    @Nested
    inner class ReassignTrainerTests {

        private val newTrainer = Trainer(
            UUID.randomUUID(),
            "newTrainerName",
            Gender.UNDEFINED,
            0,
            10
        )

        private val trainee = UserDetails(
            UUID.randomUUID(),
            "traineeName",
            "traineeMail",
            "passwordHash",
            Role.TRAINEE,
            true
        )

        @Test
        fun `should reassign trainer successfully`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { trainee }

            `when`(mockCompanyRepo.isTraineeFromCompany(trainee.id, companyId)).then { true }

            `when`(mockCompanyRepo.getTrainer(newTrainer.id, companyId)).then { newTrainer }

            `when`(mockTraineeRepo.isTraineeAssignedToTrainer(trainee.id, newTrainer.id)).then { false }

            mockCompanyService.reassignTrainer(
                companyId,
                newTrainer.id,
                trainee.id
            )
        }

        @Test
        fun `should fail if trainee not found`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { null }

            assertFailsWith<UserError.UserNotFound> {
                mockCompanyService.reassignTrainer(
                    companyId,
                    newTrainer.id,
                    trainee.id
                )
            }
        }

        @Test
        fun `should fail if trainee is not from company`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { trainee }

            `when`(mockCompanyRepo.isTraineeFromCompany(trainee.id, companyId)).then { false }

            assertFailsWith<CompanyError.TraineeNotFromCompany> {
                mockCompanyService.reassignTrainer(
                    companyId,
                    newTrainer.id,
                    trainee.id
                )
            }
        }

        @Test
        fun `should fail if new trainer not found`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { trainee }

            `when`(mockCompanyRepo.isTraineeFromCompany(trainee.id, companyId)).then { true }

            `when`(mockCompanyRepo.getTrainer(newTrainer.id, companyId)).then { null }

            assertFailsWith<CompanyError.TrainerNotFound> {
                mockCompanyService.reassignTrainer(
                    companyId,
                    newTrainer.id,
                    trainee.id
                )
            }
        }

        @Test
        fun `should fail if new trainer capacity reached`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { trainee }

            `when`(mockCompanyRepo.isTraineeFromCompany(trainee.id, companyId)).then { true }

            `when`(mockCompanyRepo.getTrainer(newTrainer.id, companyId)).then { newTrainer.copy(assignedTrainees = 10) }

            assertFailsWith<CompanyError.TrainerCapacityReached> {
                mockCompanyService.reassignTrainer(
                    companyId,
                    newTrainer.id,
                    trainee.id
                )
            }
        }

        @Test
        fun `should fail if trainee is already assigned to new trainer`() {
            `when`(mockUserRepo.getUserDetails(trainee.id)).then { trainee }

            `when`(mockCompanyRepo.isTraineeFromCompany(trainee.id, companyId)).then { true }

            `when`(mockCompanyRepo.getTrainer(newTrainer.id, companyId)).then { newTrainer }

            `when`(mockTraineeRepo.isTraineeAssignedToTrainer(trainee.id, newTrainer.id)).then { true }

            assertFailsWith<CompanyError.TrainerAlreadyAssociatedToTrainee> {
                mockCompanyService.reassignTrainer(
                    companyId,
                    newTrainer.id,
                    trainee.id
                )
            }
        }
    }

    @Nested
    inner class UpdateTrainerCapacityTests {
        private val trainer = Trainer(
            UUID.randomUUID(),
            "newTrainerName",
            Gender.UNDEFINED,
            0,
            10
        )

        @Test
        fun `should update trainer capacity successfully`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { trainer }

            mockCompanyService.updateTrainerCapacity(
                companyId,
                trainer.id,
                10
            )
        }

        @Test
        fun `should fail if trainer not found`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { null }

            assertFailsWith<CompanyError.TrainerNotFound> {
                mockCompanyService.updateTrainerCapacity(
                    companyId,
                    trainer.id,
                    10
                )
            }
        }

        @Test
        fun `should fail if capacity is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.updateTrainerCapacity(
                    companyId,
                    trainer.id,
                    -1
                )
            }
        }

        @Test
        fun `should fail if the capacity is less than the number of assigned trainees`() {
            `when`(mockCompanyRepo.getTrainer(trainer.id, companyId)).then { trainer.copy(assignedTrainees = 5) }
            
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.updateTrainerCapacity(
                    companyId,
                    trainer.id,
                    0
                )
            }
        }
    }

    @Nested
    inner class CreateCustomExerciseTests {

        @Test
        fun `should create custom exercise successfully`() {
            `when`(mockExerciseRepo.createExercise(
                "name",
                "description",
                listOf(MuscleGroup.MID_BACK),
                Modality.BODYWEIGHT,
                null
            )).then { 1 }

            mockCompanyService.createCustomExercise(
                companyId,
                "name",
                "description",
                listOf(MuscleGroup.MID_BACK),
                Modality.BODYWEIGHT,
                "www.youtube.com/watch?v=123"
            )
        }

        @Test
        fun `should fail if description is blank`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.createCustomExercise(
                    companyId,
                    "name",
                    "",
                    listOf(MuscleGroup.MID_BACK),
                    Modality.BODYWEIGHT,
                    null
                )
            }
        }

        @Test
        fun `should fail if reference is not a valid YouTube URL`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.createCustomExercise(
                    companyId,
                    "name",
                    "description",
                    listOf(MuscleGroup.MID_BACK),
                    Modality.BODYWEIGHT,
                    "invalidUrl"
                )
            }
        }
    }

    @Nested
    inner class GetExercisesTests {
        @Test
        fun `should get exercises successfully`() {
            `when`(mockExerciseRepo.getCompanyExercises(
                companyId,
                "name",
                MuscleGroup.MID_BACK,
                Modality.BODYWEIGHT,
                0,
                10
            )).then { emptyList<Exercise>() }

            `when`(mockExerciseRepo.getTotalCompanyExercises(
                companyId,
                "name",
                MuscleGroup.MID_BACK,
                Modality.BODYWEIGHT
            )).then { 0 }

            mockCompanyService.getExercises(
                companyId,
                "name",
                MuscleGroup.MID_BACK,
                Modality.BODYWEIGHT,
                0,
                10
            )
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getExercises(
                    companyId,
                    "name",
                    MuscleGroup.MID_BACK,
                    Modality.BODYWEIGHT,
                    -1,
                    10
                )
            }
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getExercises(
                    companyId,
                    "name",
                    MuscleGroup.MID_BACK,
                    Modality.BODYWEIGHT,
                    0,
                    -1
                )
            }
        }

        @Test
        fun `should fail if name is blank`() {
            assertFailsWith<IllegalArgumentException> {
                mockCompanyService.getExercises(
                    companyId,
                    "",
                    MuscleGroup.MID_BACK,
                    Modality.BODYWEIGHT,
                    0,
                    10
                )
            }
        }
    }

    @Nested
    inner class GetExerciseDetailsTests {
        @Test
        fun `should get exercise details successfully`() {
            `when`(mockExerciseRepo.getCompanyExerciseDetails(companyId, 1))
                .then {
                    ExerciseDetails(
                        1,
                        "name",
                        "description",
                        listOf(MuscleGroup.MID_BACK),
                        Modality.BODYWEIGHT,
                        null
                    )
                }

            mockCompanyService.getExerciseDetails(companyId, 1)
        }

        @Test
        fun `should fail if exercise not found`() {
            `when`(mockExerciseRepo.getCompanyExerciseDetails(companyId, 1)).then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockCompanyService.getExerciseDetails(companyId, 1)
            }
        }
    }

    private val companyId = UUID.randomUUID()

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo, mockCompanyRepo, mockTraineeRepo, mockSessionRepo, mockExerciseRepo)
    }

    companion object {
        lateinit var mockCompanyService: CompanyService

        @JvmStatic
        @BeforeAll
        fun setUp() {
            mockCompanyService = buildMockCompanyService()
        }
    }
}
