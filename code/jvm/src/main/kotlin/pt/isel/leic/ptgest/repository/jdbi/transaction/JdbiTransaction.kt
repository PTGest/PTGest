package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.leic.ptgest.repository.AuthRepo
import pt.isel.leic.ptgest.repository.CompanyRepo
import pt.isel.leic.ptgest.repository.TrainerRepo
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.WorkoutRepo
import pt.isel.leic.ptgest.repository.jdbi.auth.JdbiAuthRepo
import pt.isel.leic.ptgest.repository.jdbi.company.JdbiCompanyRepo
import pt.isel.leic.ptgest.repository.jdbi.trainer.JdbiTrainerRepo
import pt.isel.leic.ptgest.repository.jdbi.user.JdbiUserRepo
import pt.isel.leic.ptgest.repository.jdbi.workout.JdbiWorkoutRepo
import pt.isel.leic.ptgest.repository.transaction.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val authRepo: AuthRepo = JdbiAuthRepo(handle)
    override val userRepo: UserRepo = JdbiUserRepo(handle)
    override val workoutRepo: WorkoutRepo = JdbiWorkoutRepo(handle)
    override val companyRepo: CompanyRepo = JdbiCompanyRepo(handle)
    override val trainerRepo: TrainerRepo = JdbiTrainerRepo(handle)

    override fun commit() {
        handle.commit()
    }

    override fun rollback() {
        handle.rollback()
    }
}
