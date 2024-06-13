package pt.isel.leic.ptgest.http.model.trainer.response

import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback

data class GetSetSessionFeedbacks(
    val feedbacks: List<SetSessionFeedback>
)