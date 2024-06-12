package pt.isel.leic.ptgest.http.model.trainee.response

import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import java.util.*

data class GetSessionDetailsResponse(
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
) {
    constructor(session: SessionDetails) : this(
        session.id,
        session.workoutId,
        session.beginDate,
        session.endDate,
        session.location,
        session.type,
        session.notes,
        session.cancelled,
        session.reason,
        session.source
    )
}
