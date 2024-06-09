package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.*

interface SessionRepo {

    fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int
}
