package pt.isel.leic.ptgest.http.model.trainer.response

import pt.isel.leic.ptgest.domain.report.model.ReportDetails

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
