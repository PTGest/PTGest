package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
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
import pt.isel.leic.ptgest.repository.jdbi.JdbiAuthRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiCompanyRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiExerciseRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiReportRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiSessionRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiSetRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiTraineeDataRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiTraineeRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiTrainerRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiUserRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiWorkoutRepo
import pt.isel.leic.ptgest.repository.transaction.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val authRepo: AuthRepo = JdbiAuthRepo(handle)
    override val companyRepo: CompanyRepo = JdbiCompanyRepo(handle)
    override val exerciseRepo: ExerciseRepo = JdbiExerciseRepo(handle)
    override val reportRepo: ReportRepo = JdbiReportRepo(handle)
    override val sessionRepo: SessionRepo = JdbiSessionRepo(handle)
    override val setRepo: SetRepo = JdbiSetRepo(handle)
    override val traineeDataRepo: TraineeDataRepo = JdbiTraineeDataRepo(handle)
    override val traineeRepo: TraineeRepo = JdbiTraineeRepo(handle)
    override val trainerRepo: TrainerRepo = JdbiTrainerRepo(handle)
    override val userRepo: UserRepo = JdbiUserRepo(handle)
    override val workoutRepo: WorkoutRepo = JdbiWorkoutRepo(handle)

    override fun commit() {
        handle.commit()
    }

    override fun rollback() {
        handle.rollback()
    }
}
