package pt.isel.leic.ptgest.repository.transaction

import pt.isel.leic.ptgest.repository.UserRepo

interface Transaction {
    val userRepo: UserRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
