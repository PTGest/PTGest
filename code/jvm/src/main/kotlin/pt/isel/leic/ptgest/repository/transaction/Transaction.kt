package pt.isel.leic.ptgest.repository.transaction

import pt.isel.leic.ptgest.repository.AuthRepo
import pt.isel.leic.ptgest.repository.CompanyRepo
import pt.isel.leic.ptgest.repository.ExerciseRepo
import pt.isel.leic.ptgest.repository.ReportRepo
import pt.isel.leic.ptgest.repository.SessionRepo
import pt.isel.leic.ptgest.repository.SetRepo
import pt.isel.leic.ptgest.repository.TraineeDataRepo
import pt.isel.leic.ptgest.repository.TraineeRepo
import pt.isel.leic.ptgest.repository.TrainerRepo
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.WorkoutRepo

interface Transaction {
    val authRepo: AuthRepo
    val companyRepo: CompanyRepo
    val exerciseRepo: ExerciseRepo
    val reportRepo: ReportRepo
    val sessionRepo: SessionRepo
    val setRepo: SetRepo
    val traineeDataRepo: TraineeDataRepo
    val traineeRepo: TraineeRepo
    val trainerRepo: TrainerRepo
    val userRepo: UserRepo
    val workoutRepo: WorkoutRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
