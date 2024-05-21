package pt.isel.leic.ptgest.services.user

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class UserService(private val transactionManager: TransactionManager) {

    fun getTraineeDetails(userId: UUID): TraineeDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getTraineeDetails(userId) ?: throw UserError.UserNotFound
        }

    fun getTrainerDetails(userId: UUID): TrainerDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getTrainerDetails(userId) ?: throw UserError.UserNotFound
        }

    fun getCompanyDetails(userId: UUID): UserDetails =
        transactionManager.run {
            val userRepo = it.userRepo
            return@run userRepo.getUserDetails(userId) ?: throw UserError.UserNotFound
        }
}
