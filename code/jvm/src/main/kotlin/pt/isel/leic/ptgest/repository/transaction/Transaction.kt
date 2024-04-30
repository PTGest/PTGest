package pt.isel.leic.ptgest.repository.transaction

import pt.isel.leic.ptgest.repository.AuthRepo
import pt.isel.leic.ptgest.repository.CompanyRepo
import pt.isel.leic.ptgest.repository.TrainerRepo
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.WorkoutRepo

interface Transaction {
    val authRepo: AuthRepo
    val userRepo: UserRepo
    val workoutRepo: WorkoutRepo
    val companyRepo: CompanyRepo
    val trainerRepo: TrainerRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
