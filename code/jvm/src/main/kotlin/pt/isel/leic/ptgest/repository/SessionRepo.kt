package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback
import pt.isel.leic.ptgest.domain.session.model.TrainerSession
import pt.isel.leic.ptgest.domain.session.model.TrainerSessionDetails
import java.util.*

interface SessionRepo {

    fun createSession(
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    ): Int

    fun getTrainerSessions(
        trainerId: UUID,
        skip: Int,
        limit: Int?
    ): List<TrainerSession>

    fun getTotalTrainerSessions(
        trainerId: UUID
    ): Int

    fun getTraineeSessions(
        traineeId: UUID?,
        skip: Int,
        limit: Int?,
        sessionType: SessionType?
    ): List<Session>

    fun getTotalTraineeSessions(
        traineeId: UUID?,
        sessionType: SessionType?
    ): Int

    fun getSessionTrainee(
        sessionId: Int
    ): UUID

    fun getTrainerSessionDetails(
        sessionId: Int
    ): TrainerSessionDetails?

    fun getSessionDetails(
        sessionId: Int
    ): SessionDetails?

    fun getSessionDetails(
        traineeId: UUID,
        sessionId: Int
    ): SessionDetails?

    fun updateSession(
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    )

    fun cancelSession(sessionId: Int, source: Source, reason: String?)

    fun createFeedback(
        source: Source,
        feedback: String,
        date: Date
    ): Int

    fun getSessionFeedbacks(
        sessionId: Int
    ): List<SessionFeedback>

    fun getSessionFeedback(
        feedbackId: Int,
        sessionId: Int
    ): SessionFeedback?

    fun getSetSessionFeedbacks(
        sessionId: Int
    ): List<SetSessionFeedback>

    fun getSetSessionFeedback(
        feedbackId: Int,
        sessionId: Int,
        workoutId: Int,
        setOrderId: Int,
        setId: Int
    ): SetSessionFeedback?

    fun createSessionFeedback(
        feedbackId: Int,
        sessionId: Int
    )

    fun validateSessionSet(
        sessionId: Int,
        setOrderId: Int,
        setId: Int
    ): Boolean

    fun createSessionSetFeedback(
        feedbackId: Int,
        sessionId: Int,
        workoutId: Int,
        setOrderId: Int,
        setId: Int
    )

    fun editFeedback(
        feedbackId: Int,
        feedback: String,
        date: Date
    )
}
