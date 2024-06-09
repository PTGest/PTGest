package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import java.util.*

interface TraineeRepo {

    fun getTraineeDetails(traineeId: UUID): TraineeDetails?

    fun getTrainerAssigned(traineeId: UUID): UUID?
}
