package pt.isel.leic.ptgest.repository.transaction

import pt.isel.leic.ptgest.repository.AuthRepo

interface Transaction {
    val authRepo: AuthRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
