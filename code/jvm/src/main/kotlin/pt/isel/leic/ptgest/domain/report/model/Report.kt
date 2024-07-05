package pt.isel.leic.ptgest.domain.report.model

import java.util.UUID

data class Report(
    val id: Int,
    val date: String,
    val visibility: Boolean
)
