package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.trainee.model.TraineeDetails
import pt.isel.leic.ptgest.domain.trainer.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.UserError
import java.util.*

@Service
class UserService(private val transactionManager: TransactionManager) {
    fun getUserDetails(userId: UUID): UserDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getUserDetails(userId) ?: throw UserError.UserNotFound
        }

    fun getTraineeDetails(traineeId: UUID): TraineeDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            return@run traineeRepo.getTraineeDetails(traineeId) ?: throw UserError.UserNotFound
        }

    fun getTrainerDetails(trainerId: UUID): TrainerDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            return@run trainerRepo.getTrainerDetails(trainerId) ?: throw UserError.UserNotFound
        }
}
