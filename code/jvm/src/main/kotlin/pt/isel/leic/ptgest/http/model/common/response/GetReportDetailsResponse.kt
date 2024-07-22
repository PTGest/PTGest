package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import java.util.Date

data class GetReportDetailsResponse(
    val trainee: String,
    val date: Date,
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
