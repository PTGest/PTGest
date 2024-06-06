package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.trainer.model.Report

data class GetReportsResponse(
    val reports: List<Report>,
    val total: Int
)
