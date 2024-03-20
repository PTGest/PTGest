package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Component
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager

@Component
class JdbiTransactionManager(
    private val jdbi: Jdbi
) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }

    override fun <R> runWithLevel(level: TransactionIsolationLevel, block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception>(level) {
            val transaction = JdbiTransaction(it)
            block(transaction)
        }
}