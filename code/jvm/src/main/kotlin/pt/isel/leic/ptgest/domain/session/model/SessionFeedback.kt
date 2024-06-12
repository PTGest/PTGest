package pt.isel.leic.ptgest.domain.session.model

import pt.isel.leic.ptgest.domain.common.Source
import java.util.*

data class SessionFeedback(
    val id: Int,
    val source: Source,
    val feedback: String,
    val date: Date
)
