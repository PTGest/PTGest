package pt.isel.leic.ptgest.http.model.trainer.request

import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.*

data class EditSessionRequest(
    val workoutId: Int,
    val beginDate: Date,
    val endDate: Date?,
    val location: String?,
    val type: SessionType,
    val notes: String?
)
