package pt.isel.leic.ptgest.services.user

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class UserService(private val transactionManager: TransactionManager) {
    fun getUserDetails(userId: UUID): UserDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getUserDetails(userId) ?: throw UserError.UserNotFound
        }

    fun getTraineeDetails(traineeId: UUID): TraineeDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getTraineeDetails(traineeId) ?: throw UserError.UserNotFound
        }

    fun getTrainerDetails(trainerId: UUID): TrainerDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getTrainerDetails(trainerId) ?: throw UserError.UserNotFound
        }
}
