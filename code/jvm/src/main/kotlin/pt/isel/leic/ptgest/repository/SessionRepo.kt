package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
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
    ): Session?

    fun updateSession(
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    )

    fun cancelSession(sessionId: Int, reason: String?)
}
