package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import pt.isel.leic.ptgest.domain.common.SessionType
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
