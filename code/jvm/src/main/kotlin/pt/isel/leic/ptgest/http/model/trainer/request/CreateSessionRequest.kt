package pt.isel.leic.ptgest.http.model.trainer.request

import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.Date
import java.util.UUID

data class CreateSessionRequest(
    val traineeId: UUID,
    val workoutId: Int,
    val beginDate: Date,
    val endDate: Date?,
    val type: SessionType,
    val notes: String?
)
