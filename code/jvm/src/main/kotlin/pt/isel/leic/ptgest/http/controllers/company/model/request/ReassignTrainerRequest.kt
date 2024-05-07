package pt.isel.leic.ptgest.http.controllers.company.model.request

import java.util.UUID

data class ReassignTrainerRequest(
    val newTrainerId: UUID
)
