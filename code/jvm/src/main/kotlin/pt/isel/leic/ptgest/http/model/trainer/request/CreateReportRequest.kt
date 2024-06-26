package pt.isel.leic.ptgest.http.model.trainer.request

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class CreateReportRequest(
    val traineeId: UUID,
    @field:NotEmpty(message = "Report cannot be empty")
    val report: String,
    val visibility: Boolean
)
