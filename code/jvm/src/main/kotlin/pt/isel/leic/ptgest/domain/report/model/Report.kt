package pt.isel.leic.ptgest.domain.report.model

import java.util.Date

data class Report(
    val id: Int,
    val date: Date,
    val visibility: Boolean
)
