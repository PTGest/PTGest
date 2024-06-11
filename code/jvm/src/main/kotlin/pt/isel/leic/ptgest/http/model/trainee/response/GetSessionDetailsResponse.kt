package pt.isel.leic.ptgest.http.model.trainee.response

import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import java.util.*

data class GetSessionDetailsResponse(
    val id: Int,
    val beginDate: Date,
    val endDate: Date?,
    val type: SessionType
) {
    constructor(session: Session) : this(
        session.id,
        session.beginDate,
        session.endDate,
        session.type
    )
}
