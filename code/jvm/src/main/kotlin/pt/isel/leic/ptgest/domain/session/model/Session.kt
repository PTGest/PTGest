package pt.isel.leic.ptgest.domain.session.model

import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.*

data class Session(
    val id: Int,
    val beginDate: Date,
    val endDate: Date?,
    val type: SessionType,
    val cancelled: Boolean = false
)
