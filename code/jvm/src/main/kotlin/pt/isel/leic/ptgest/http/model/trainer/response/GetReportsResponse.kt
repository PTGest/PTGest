package pt.isel.leic.ptgest.http.model.trainer.response

import pt.isel.leic.ptgest.domain.report.model.Report

data class GetReportsResponse(
    val reports: List<Report>,
    val total: Int
)
