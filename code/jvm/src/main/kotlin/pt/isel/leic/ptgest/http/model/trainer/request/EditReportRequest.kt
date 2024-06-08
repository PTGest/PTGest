package pt.isel.leic.ptgest.http.model.trainer.request

import jakarta.validation.constraints.NotEmpty

data class EditReportRequest(
    @field:NotEmpty(message = "Report cannot be empty")
    val report: String,
    val visibility: Boolean
)
