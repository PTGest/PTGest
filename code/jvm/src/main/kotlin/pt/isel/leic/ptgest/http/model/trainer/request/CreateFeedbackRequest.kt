package pt.isel.leic.ptgest.http.model.trainer.request

import jakarta.validation.constraints.NotEmpty

data class CreateFeedbackRequest(
    @field:NotEmpty(message = "Feedback cannot be empty")
    val feedback: String
)
