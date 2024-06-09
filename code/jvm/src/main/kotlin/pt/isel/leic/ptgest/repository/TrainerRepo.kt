package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import java.util.*

interface TrainerRepo {

    fun getTrainerDetails(trainerId: UUID): TrainerDetails?

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID?

    fun associateTrainerToReport(trainerId: UUID, reportId: Int)

    fun getTraineeIdByName(name: String): UUID?

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)
}
