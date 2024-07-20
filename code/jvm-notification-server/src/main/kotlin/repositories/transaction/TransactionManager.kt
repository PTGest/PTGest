package repositories.transaction

import org.jdbi.v3.core.transaction.TransactionIsolationLevel

interface TransactionManager {
    /**
     * Runs the given block in a transaction, with the default isolation level.
     */
    fun <R> run(block: (Transaction) -> R): R

    /**
     * Runs the given block in a transaction, with the given isolation level.
     * Gives a more fine-grained control over the transaction.
     */
    fun <R> runWithLevel(level: TransactionIsolationLevel, block: (Transaction) -> R): R
}
