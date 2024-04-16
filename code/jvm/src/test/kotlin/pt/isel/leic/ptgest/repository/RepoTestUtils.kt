package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.leic.ptgest.ServerConfig
import pt.isel.leic.ptgest.repository.jdbi.configureWithAppRequirements

fun <R> asTransaction(jdbi: Jdbi, block: (Handle) -> R): R =
    jdbi.inTransaction<R, Exception> { handle -> block(handle) }

fun <R> asTransactionWithLevel(jdbi: Jdbi, isolationLevel: TransactionIsolationLevel, block: (Handle) -> R): R =
    jdbi.inTransaction<R, Exception>(isolationLevel) { handle -> block(handle) }

fun getDevJdbi(): Jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(ServerConfig.dbUrl)
        currentSchema = "dev"
    }
).configureWithAppRequirements()

fun Handle.cleanup() {
    createUpdate(
        """
        truncate
            dev.company,
            dev.company_pt,
            dev.exercise,
            dev.feedback,
            dev.password_reset_token,
            dev.personal_trainer,
            dev.pt_trainee,
            dev.refresh_token,
            dev.report,
            dev.session,
            dev.session_feedback,
            dev.token,
            dev.trainee,
            dev.trainee_data,
            dev.trainer_favorite_exercise,
            dev.trainer_favorite_workout,
            dev."user",
            dev.workout_exercise_feedback,
            dev.workout_plan,
            dev.workout_plan_exercise;
        
        alter sequence dev.exercise_id_seq restart with 1;
        alter sequence dev.feedback_id_seq restart with 1;
        alter sequence dev.workout_plan_id_seq restart with 1;
        """.trimIndent()
    ).execute()
}
