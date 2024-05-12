package pt.isel.leic.ptgest.http.controllers.company.model.response

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.company.model.Trainer
import java.util.UUID

data class TrainerResponse(
    val id: UUID,
    val name: String,
    val gender: Gender,
    val capacity: Int,
    val assignedTrainees: Int

) {
    constructor(trainer: Trainer) :
        this(trainer.id, trainer.name, trainer.gender, trainer.capacity, trainer.totalTrainees)
}
