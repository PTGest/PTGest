package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSetDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.InaccessibleRecourse
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.*

@Service
class TraineeService(
    private val transactionManager: TransactionManager
) {
    fun getReports(
        traineeId: UUID,
        skip: Int?,
        limit: Int?
    ): Pair<List<Report>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val reportRepo = it.reportRepo

            val reports = reportRepo.getTraineeReports(traineeId, skip ?: 0, limit)
            val totalReports = reportRepo.getTotalTraineeReports(traineeId)

            return@run Pair(reports, totalReports)
        }
    }

    fun getReportDetails(
        traineeId: UUID,
        reportId: Int
    ): ReportDetails =
        transactionManager.run {
            val reportRepo = it.reportRepo

            val report = reportRepo.getReportDetails(traineeId, reportId)
                ?: throw ResourceNotFoundError

            if (!report.visibility) {
                throw InaccessibleRecourse
            }

            return@run report
        }

    fun getTraineeDataHistory(
        traineeId: UUID,
        order: Order,
        skip: Int?,
        limit: Int?
    ): Pair<List<TraineeData>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val traineeDataRepo = it.traineeDataRepo

            val traineeData = traineeDataRepo.getTraineeData(
                traineeId,
                order,
                skip ?: 0,
                limit
            )

            val totalTraineeData = traineeDataRepo.getTotalTraineeData(traineeId)

            return@run Pair(traineeData, totalTraineeData)
        }
    }

    fun getTraineeDataDetails(
        traineeId: UUID,
        dataId: Int
    ): TraineeDataDetails =
        transactionManager.run {
            val traineeDataRepo = it.traineeDataRepo

            return@run traineeDataRepo.getTraineeBodyDataDetails(traineeId, dataId)
                ?: throw ResourceNotFoundError
        }

    fun getExerciseDetails(
        traineeId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            exerciseRepo.getExerciseDetails(exerciseId)
                ?: throw ResourceNotFoundError
        }

    fun getSetDetails(
        traineeId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val setRepo = it.setRepo

            val set = setRepo.getSetDetails(setId)
                ?: throw ResourceNotFoundError

            val exercises = setRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun getWorkoutDetails(
        traineeId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo

            val workout = workoutRepo.getWorkoutDetails(workoutId)
                ?: throw ResourceNotFoundError

            val sets = workoutRepo.getWorkoutSetIds(workoutId).mapNotNull { setId ->
                val set = setRepo.getWorkoutSet(workoutId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                WorkoutSetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    fun getSessions(
        traineeId: UUID,
        sessionType: SessionType?,
        date: Date?,
        skip: Int?,
        limit: Int?
    ): Pair<List<Session>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessions = sessionRepo.getTraineeSessions(traineeId, sessionType, date, skip ?: 0, limit)
            val total = sessionRepo.getTotalTraineeSessions(traineeId, sessionType, date)

            return@run Pair(sessions, total)
        }
    }

    fun getSessionDetails(
        traineeId: UUID,
        sessionId: Int
    ): Pair<SessionDetails, List<SessionFeedback>> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessionDetails = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            val feedbacks = sessionRepo.getSessionFeedbacks(sessionId)

            return@run Pair(sessionDetails, feedbacks)
        }

    fun cancelSession(
        traineeId: UUID,
        sessionId: Int,
        reason: String?
    ) {
        Validators.validate(
            Validators.ValidationRequest(reason, "Reason must be a non-empty string.") { it is String && it.isNotBlank() }
        )

        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            require(Validators.isCurrentDate24BeforeDate(session.beginDate)) { "Session can only be cancelled 24 hours before the session begin date." }

            sessionRepo.cancelSession(sessionId, Source.TRAINEE, reason)
        }
    }

    fun createSessionFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be created only after the session begin date." }

            sessionRepo.createFeedback(Source.TRAINEE, feedback, requestDate).also { feedbackId ->
                sessionRepo.createSessionFeedback(feedbackId, sessionId)
            }
        }
    }

    fun editSessionFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedbackId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be edited only after the session begin date." }

            sessionRepo.getSessionFeedback(feedbackId, sessionId)
                ?: throw ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
        }
    }

    fun createSessionSetFeedback(
        traineeId: UUID,
        sessionId: Int,
        setOrderId: Int,
        setId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be created only after the session begin date." }

            sessionRepo.createFeedback(Source.TRAINEE, feedback, requestDate).also { feedbackId ->
                sessionRepo.createSessionSetFeedback(feedbackId, sessionId, session.workoutId, setOrderId, setId)
            }
        }
    }

    fun editSessionSetFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedbackId: Int,
        setOrderId: Int,
        setId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be edited only after the session begin date." }
            require(sessionRepo.validateSessionSet(sessionId, setId, setOrderId)) { "Set must be part of the session." }

            sessionRepo.getSetSessionFeedback(feedbackId, sessionId, setOrderId, setId)
                ?: throw ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
        }
    }

    fun getSetSessionFeedbacks(
        traineeId: UUID,
        sessionId: Int
    ): List<SetSessionFeedback> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw ResourceNotFoundError

            sessionRepo.getSetSessionFeedbacks(sessionId)
        }
}
