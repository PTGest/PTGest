package pt.isel.leic.ptgest.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SessionSetFeedback
import pt.isel.leic.ptgest.domain.set.model.Set
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.set.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.traineeData.model.BodyCircumferences
import pt.isel.leic.ptgest.domain.traineeData.model.BodyComposition
import pt.isel.leic.ptgest.domain.traineeData.model.BodyData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSet
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSetDetails
import pt.isel.leic.ptgest.service.MockServices.buildMockTraineeService
import pt.isel.leic.ptgest.services.TraineeService
import pt.isel.leic.ptgest.services.errors.InaccessibleRecourse
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TraineeServiceTests {

    private val mockReportRepo = MockRepos.mockReportRepo
    private val mockTraineeDataRepo = MockRepos.mockTraineeDataRepo
    private val mockExerciseRepo = MockRepos.mockExerciseRepo
    private val mockSetRepo = MockRepos.mockSetRepo
    private val mockWorkoutRepo = MockRepos.mockWorkoutRepo
    private val mockSessionRepo = MockRepos.mockSessionRepo

    @Nested
    inner class GetReportsTests {
        @Test
        fun `should return reports successfully`() {
            `when`(mockReportRepo.getTraineeReports(traineeId, 0, null))
                .then { emptyList<Report>() }

            `when`(mockReportRepo.getTotalTraineeReports(traineeId))
                .then { 0 }

            mockTrainneService.getReports(traineeId, null, null)
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getReports(traineeId, null, -1)
            }
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getReports(traineeId, -1, null)
            }
        }
    }

    @Nested
    inner class GetReportDetailsTests {
        val reportDetails = ReportDetails(
            trainer = "trainer",
            trainee = "trainee",
            date = Date(),
            report = "report",
            visibility = true
        )

        @Test
        fun `should return report details successfully`() {
            `when`(mockReportRepo.getReportDetails(traineeId, 1))
                .then { reportDetails }

            mockTrainneService.getReportDetails(traineeId, 1)
        }

        @Test
        fun `should fail if report does not exist`() {
            `when`(mockReportRepo.getReportDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getReportDetails(traineeId, 1)
            }
        }

        @Test
        fun `should fail if report is not visible`() {
            `when`(mockReportRepo.getReportDetails(traineeId, 1))
                .then { reportDetails.copy(visibility = false) }

            assertFailsWith<InaccessibleRecourse> {
                mockTrainneService.getReportDetails(traineeId, 1)
            }
        }
    }

    @Nested
    inner class GetTraineeDataHistoryTests {
        @Test
        fun `should return trainee data history successfully`() {
            `when`(mockTraineeDataRepo.getTraineeData(traineeId, Order.ASC, 0, null))
                .then { emptyList<Report>() }

            `when`(mockTraineeDataRepo.getTotalTraineeData(traineeId))
                .then { 0 }

            mockTrainneService.getTraineeDataHistory(traineeId, Order.ASC, 0, null)
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getTraineeDataHistory(traineeId, Order.ASC, 0, -1)
            }
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getTraineeDataHistory(traineeId, Order.ASC, -1, 0)
            }
        }
    }

    @Nested
    inner class GetTraineeDataDetailsTests {
        private val traineeDataDetails = TraineeDataDetails(
            date = Date(),
            bodyData = BodyData(
                weight = 70.0,
                height = 1.75,
                bodyCircumferences = BodyCircumferences(
                    neck = 40.0,
                    chest = 100.0,
                    waist = 80.0,
                    hips = 100.0,
                    thighs = 60.0,
                    calves = 40.0,
                    relaxedRightArm = 30.0,
                    relaxedLeftArm = 30.0,
                    flexedRightArm = 35.0,
                    flexedLeftArm = 35.0,
                    forearm = 25.0
                ),
                bodyComposition = BodyComposition(
                    20.0,
                    20.0,
                    20.0
                ),
                null
            )
        )

        @Test
        fun `should return trainee data details successfully`() {
            `when`(mockTraineeDataRepo.getTraineeBodyDataDetails(traineeId, 1))
                .then { traineeDataDetails }

            mockTrainneService.getTraineeDataDetails(traineeId, 1)
        }

        @Test
        fun `should fail if trainee data does not exist`() {
            `when`(mockTraineeDataRepo.getTraineeBodyDataDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getTraineeDataDetails(traineeId, 1)
            }
        }
    }

    @Nested
    inner class GetExerciseDetailsTests {
        private val exerciseDetails = ExerciseDetails(
            id = 1,
            name = "exercise",
            description = "description",
            muscleGroup = listOf(MuscleGroup.MID_BACK),
            modality = Modality.BODYWEIGHT,
            ref = null
        )

        @Test
        fun `should return exercise details successfully`() {
            `when`(mockExerciseRepo.getExerciseDetails(1))
                .then { exerciseDetails }

            mockTrainneService.getExerciseDetails(traineeId, 1)
        }

        @Test
        fun `should fail if exercise does not exist`() {
            `when`(mockExerciseRepo.getExerciseDetails(1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getExerciseDetails(traineeId, 1)
            }
        }
    }

    @Nested
    inner class GetSetDetailsTests {
        private val setDetails = SetDetails(
            id = 1,
            name = "set",
            notes = "notes",
            type = SetType.SIMPLESET,
            setExerciseDetails = emptyList()
        )

        private val set = Set(
            id = 1,
            name = "set",
            notes = "notes",
            type = SetType.SIMPLESET
        )

        @Test
        fun `should return set details successfully`() {
            `when`(mockSetRepo.getSetDetails(1))
                .then { set }

            `when`(mockSetRepo.getSetExercises(1))
                .then { emptyList<SetExerciseDetails>() }

            val result = mockTrainneService.getSetDetails(traineeId, 1)

            assertEquals(setDetails, result)
        }

        @Test
        fun `should fail if set does not exist`() {
            `when`(mockSetRepo.getSetDetails(1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getSetDetails(traineeId, 1)
            }
        }
    }

    @Nested
    inner class GetWorkoutDetailsTests {
        private val workout = Workout(
            id = 1,
            name = "workout",
            description = "description",
            muscleGroup = listOf(MuscleGroup.MID_BACK)
        )

        private val workoutSet = WorkoutSet(
            id = 1,
            orderId = 1,
            name = "set",
            notes = "notes",
            type = SetType.SIMPLESET
        )

        private val workoutDetails = WorkoutDetails(
            workout,
            listOf(WorkoutSetDetails(workoutSet, emptyList<SetExerciseDetails>()))
        )

        @Test
        fun `should return workout details successfully`() {
            `when`(mockWorkoutRepo.getWorkoutDetails(1))
                .then { workout }

            `when`(mockWorkoutRepo.getWorkoutSetIds(1))
                .then { listOf(1) }

            `when`(mockSetRepo.getWorkoutSet(1, 1))
                .then { workoutSet }

            `when`(mockSetRepo.getSetExercises(1))
                .then { emptyList<SetExerciseDetails>() }

            val result = mockTrainneService.getWorkoutDetails(traineeId, 1)

            assertEquals(workoutDetails, result)
        }

        @Test
        fun `should fail if workout does not exist`() {
            `when`(mockWorkoutRepo.getWorkoutDetails(1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getWorkoutDetails(traineeId, 1)
            }
        }

        @Test
        fun `should return workout details successfully with missing set`() {
            `when`(mockWorkoutRepo.getWorkoutDetails(1))
                .then { workout }

            `when`(mockWorkoutRepo.getWorkoutSetIds(1))
                .then { listOf(1) }

            `when`(mockSetRepo.getWorkoutSet(1, 1))
                .then { null }

            mockTrainneService.getWorkoutDetails(traineeId, 1)
        }
    }

    @Nested
    inner class GetSessionsTests {
        @Test
        fun `should return sessions successfully`() {
            `when`(mockSessionRepo.getTraineeSessions(traineeId, null, null, 0, null))
                .then { emptyList<Session>() }

            `when`(mockSessionRepo.getTotalTraineeSessions(traineeId, null, null))
                .then { 0 }

            mockTrainneService.getSessions(traineeId, null, null, null, null)
        }

        @Test
        fun `should fail if limit is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getSessions(traineeId, null, null, null, -1)
            }
        }

        @Test
        fun `should fail if skip is negative`() {
            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.getSessions(traineeId, null, null, -1, 0)
            }
        }
    }

    @Nested
    inner class GetSessionDetailsTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Date(),
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should return session details successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.getSessionFeedbacks(1))
                .then { emptyList<SessionFeedback>() }

            val result = mockTrainneService.getSessionDetails(traineeId, 1)

            assertEquals(sessionDetails, result.first)
            assertEquals(emptyList<SessionFeedback>(), result.second)
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getSessionDetails(traineeId, 1)
            }
        }
    }

    @Nested
    inner class CancelSessionTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 24) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should cancel session successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            mockTrainneService.cancelSession(traineeId, 1, "reason")
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.cancelSession(traineeId, 1, "reason")
            }
        }

        @Test
        fun `should fail if session is already cancelled`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(cancelled = true) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.cancelSession(traineeId, 1, "reason")
            }
        }

        @Test
        fun `should fail if session is in the present`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(beginDate = Date()) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.cancelSession(traineeId, 1, "reason")
            }
        }
    }

    @Nested
    inner class CreateSessionFeedbackTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should create session feedback successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            mockTrainneService.createSessionFeedback(traineeId, 1, "feedback")
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.createSessionFeedback(traineeId, 1, "feedback")
            }
        }

        @Test
        fun `should fail if session is in the future`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.createSessionFeedback(traineeId, 1, "feedback")
            }
        }

        @Test
        fun `should fail if session is already cancelled`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(cancelled = true) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.createSessionFeedback(traineeId, 1, "feedback")
            }
        }
    }

    @Nested
    inner class EditSessionFeedbackTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        private val sessionFeedback = SessionFeedback(
            id = 1,
            source = Source.TRAINEE,
            feedback = "feedback",
            Date()
        )

        @Test
        fun `should edit session feedback successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.getSessionFeedback(1, 1))
                .then { sessionFeedback }

            mockTrainneService.editSessionFeedback(traineeId, 1, 1, "new feedback")
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.editSessionFeedback(traineeId, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if session feedback does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.getSessionFeedback(1, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.editSessionFeedback(traineeId, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if session is in the future`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.editSessionFeedback(traineeId, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if session is already cancelled`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(cancelled = true) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.editSessionFeedback(traineeId, 1, 1, "new feedback")
            }
        }
    }

    @Nested
    inner class CreateSessionSetFeedbackTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should create session set feedback successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.validateSessionSet(1, 1, 1))
                .then { true }

            mockTrainneService.createSessionSetFeedback(traineeId, 1, 1, 1, "feedback")
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.createSessionSetFeedback(traineeId, 1, 1, 1, "feedback")
            }
        }

        @Test
        fun `should fail if session is in the future`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.createSessionSetFeedback(traineeId, 1, 1, 1, "feedback")
            }
        }

        @Test
        fun `should fail if session is already cancelled`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(cancelled = true) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.createSessionSetFeedback(traineeId, 1, 1, 1, "feedback")
            }
        }

        @Test
        fun `should fail if set does not exist or does not belong to the session`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.validateSessionSet(1, 1, 1))
                .then { false }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.createSessionSetFeedback(traineeId, 1, 1, 1, "feedback")
            }
        }
    }

    @Nested
    inner class EditSessionSetFeedbackTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should edit session set feedback successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.validateSessionSet(1, 1, 1))
                .then { true }

            mockTrainneService.editSessionSetFeedback(traineeId, 1, 1, 1, 1, "new feedback")
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.editSessionSetFeedback(traineeId, 1, 1, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if session is in the future`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.editSessionSetFeedback(traineeId, 1, 1, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if session is already cancelled`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails.copy(cancelled = true) }

            assertFailsWith<IllegalArgumentException> {
                mockTrainneService.editSessionSetFeedback(traineeId, 1, 1, 1, 1, "new feedback")
            }
        }

        @Test
        fun `should fail if set does not exist or does not belong to the session`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.validateSessionSet(1, 1, 1))
                .then { false }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.editSessionSetFeedback(traineeId, 1, 1, 1, 1, "new feedback")
            }
        }
    }

    @Nested
    inner class GetSessionSetFeedbackTests {
        private val sessionDetails = SessionDetails(
            id = 1,
            workoutId = 1,
            beginDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            endDate = null,
            location = "location",
            type = SessionType.PLAN_BASED,
            notes = "notes",
            cancelled = false,
            reason = null,
            source = null
        )

        @Test
        fun `should return session set feedback successfully`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { sessionDetails }

            `when`(mockSessionRepo.getSessionSetFeedbacks(1))
                .then { emptyList<SessionSetFeedback>() }

            mockTrainneService.getSessionSetFeedbacks(traineeId, 1)
        }

        @Test
        fun `should fail if session does not exist`() {
            `when`(mockSessionRepo.getSessionDetails(traineeId, 1))
                .then { null }

            assertFailsWith<ResourceNotFoundError> {
                mockTrainneService.getSessionSetFeedbacks(traineeId, 1)
            }
        }
    }

    private val traineeId = UUID.randomUUID()

    @AfterEach
    fun cleanUp() {
        reset(
            mockReportRepo,
            mockTraineeDataRepo,
            mockExerciseRepo,
            mockSetRepo,
            mockWorkoutRepo,
            mockSessionRepo
        )
    }

    companion object {
        lateinit var mockTrainneService: TraineeService

        @JvmStatic
        @BeforeAll
        fun setUp() {
            mockTrainneService = buildMockTraineeService()
        }
    }
}
