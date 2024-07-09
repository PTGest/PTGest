package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.user.model.UserDetails
import java.util.*

interface UserRepo {

    fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID)

    fun associateTraineeToCompany(traineeId: UUID, companyId: UUID)

    fun getUserDetails(email: String): UserDetails?

    fun userExists(email: String): Boolean

    fun getUserDetails(userId: UUID): UserDetails?
}
