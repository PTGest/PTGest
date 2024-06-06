package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.trainer.model.ReportDetails

data class GetReportDetailsResponse(
    val trainee: String,
    val date: String,
    val report: String,
    val visibility: Boolean
) {
    constructor(reportDetails: ReportDetails) : this(
        reportDetails.trainee,
        reportDetails.date,
        reportDetails.report,
        reportDetails.visibility
    )
}
