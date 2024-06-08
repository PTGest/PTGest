package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import java.util.UUID

interface UserRepo {

    fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID)

    fun associateTraineeToCompany(traineeId: UUID, companyId: UUID)

    fun getUserDetails(email: String): UserDetails?

    fun getUserDetails(userId: UUID): UserDetails?

    fun getTraineeDetails(traineeId: UUID): TraineeDetails?

    fun getTrainerDetails(trainerId: UUID): TrainerDetails?
}
