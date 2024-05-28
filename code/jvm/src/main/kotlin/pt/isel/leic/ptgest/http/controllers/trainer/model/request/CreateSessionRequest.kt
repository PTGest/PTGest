package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import pt.isel.leic.ptgest.domain.common.model.SessionType
import java.util.UUID
import java.util.Date

data class CreateSessionRequest(
    val traineeId: UUID,
    val workoutId: Int,
    val beginDate: Date,
    val endDate: Date?,
    val type: SessionType,
    val notes: String?
)