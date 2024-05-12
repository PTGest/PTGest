package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import java.util.UUID

interface UserRepo {

    fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID)

    fun associateTraineeToCompany(traineeId: UUID, companyId: UUID)

    fun getUserDetails(email: String): UserDetails?

    fun getUserDetails(userId: UUID): UserDetails?

    fun getTraineeDetails(userId: UUID): TraineeDetails?

    fun getTrainerDetails(userId: UUID): TrainerDetails?
}
