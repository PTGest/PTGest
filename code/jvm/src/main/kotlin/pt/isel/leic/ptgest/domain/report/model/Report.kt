package pt.isel.leic.ptgest.domain.report.model

data class Report(
    val id: Int,
    val trainee: String,
    val date: String,
    val visibility: Boolean
)
