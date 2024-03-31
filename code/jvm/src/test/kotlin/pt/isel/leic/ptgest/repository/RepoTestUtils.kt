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
            dev.company_pt,
            dev.company,
            dev.pt_trainee,
            dev.trainee_data,
            dev.report,
            dev.trainer_favorite_exercise,
            dev.session_feedback,
            dev.session_exercise_feedback,
            dev.session_exercise,
            dev.session,
            dev.trainee,
            dev.exercise,
            dev.personal_trainer,
            dev."user",
            dev.feedback;
        """.trimIndent()
    ).execute()
}
