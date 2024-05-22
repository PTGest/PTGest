package pt.isel.leic.ptgest.repository

import java.util.UUID

interface TraineeRepo {

    fun getTrainerAssigned(traineeId: UUID): UUID?
}
