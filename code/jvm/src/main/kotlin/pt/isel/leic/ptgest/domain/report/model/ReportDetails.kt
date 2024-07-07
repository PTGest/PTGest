package pt.isel.leic.ptgest.domain.report.model

data class ReportDetails(
    val trainer: String,
    val trainee: String,
    val date: String,
    val report: String,
    val visibility: Boolean
)
