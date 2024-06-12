package pt.isel.leic.ptgest.domain.session.model

import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.*

data class SessionDetails(
    val id: Int,
    val workoutId: Int,
    val beginDate: Date,
    val endDate: Date?,
    val location: String?,
    val type: SessionType,
    val notes: String?,
    val cancelled: Boolean,
    val reason: String?,
    val source: Source
)
