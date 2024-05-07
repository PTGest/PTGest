package pt.isel.leic.ptgest.http.controllers.company.model.response

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.company.model.Trainer
import pt.isel.leic.ptgest.http.media.Uris.User.userDetails

data class TrainerResponse(
    val ref: String,
    val name: String,
    val gender: Gender
) {
    companion object {
        fun Trainer.toResponse() = TrainerResponse(
            ref = userDetails(id),
            name = name,
            gender = gender
        )
    }
}
