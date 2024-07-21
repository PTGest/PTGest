package pt.isel.leic.ptgest.domain.report.model

import java.util.Date

data class ReportDetails(
    val trainer: String,
    val trainee: String,
    val date: Date,
    val report: String,
    val visibility: Boolean
)
