package pt.isel.leic.ptgest.services.user

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.user.TraineeDetails
import pt.isel.leic.ptgest.domain.user.TrainerDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

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
