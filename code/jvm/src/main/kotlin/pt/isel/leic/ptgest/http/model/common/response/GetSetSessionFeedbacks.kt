package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.session.model.SessionSetFeedback

data class GetSetSessionFeedbacks(
    val feedbacks: List<SessionSetFeedback>
)
