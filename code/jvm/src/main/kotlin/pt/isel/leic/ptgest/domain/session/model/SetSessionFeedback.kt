package pt.isel.leic.ptgest.domain.session.model

import pt.isel.leic.ptgest.domain.common.Source
import java.util.*

data class SetSessionFeedback(
    val id: Int,
    val setOrderId: Int,
    val setId: Int,
    val source: Source,
    val feedback: String,
    val date: Date
)
