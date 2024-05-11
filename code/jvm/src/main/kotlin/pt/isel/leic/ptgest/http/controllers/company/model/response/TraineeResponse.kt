package pt.isel.leic.ptgest.http.controllers.company.model.response

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.company.model.Trainee
import java.util.UUID

data class TraineeResponse(
    val id: UUID,
    val name: String,
    val gender: Gender
) {
    constructor(trainer: Trainee) :
        this(trainer.id, trainer.name, trainer.gender)
}
