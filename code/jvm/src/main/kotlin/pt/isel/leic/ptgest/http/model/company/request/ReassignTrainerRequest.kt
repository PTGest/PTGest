package pt.isel.leic.ptgest.http.model.company.request

import java.util.UUID

data class ReassignTrainerRequest(
    val newTrainerId: UUID
)
