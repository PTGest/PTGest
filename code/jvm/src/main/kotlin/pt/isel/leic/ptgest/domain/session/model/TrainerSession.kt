package pt.isel.leic.ptgest.domain.session.model

import pt.isel.leic.ptgest.domain.session.SessionType
import java.util.*

data class TrainerSession(
    val id: Int,
    val traineeName: String,
    val beginDate: Date,
    val endDate: Date?,
    val type: SessionType
)
