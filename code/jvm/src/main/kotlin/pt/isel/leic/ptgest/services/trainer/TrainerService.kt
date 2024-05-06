package pt.isel.leic.ptgest.services.trainer

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {

    fun associateTrainerToExercise(
        trainerId: UUID,
        exerciseId: Int
    ) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            trainerRepo.associateTrainerToExercise(trainerId, exerciseId)
        }
    }

    fun associateTrainerToSet(
        trainerId: UUID,
        setId: Int
    ) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            trainerRepo.associateTrainerToSet(trainerId, setId)
        }
    }
}
