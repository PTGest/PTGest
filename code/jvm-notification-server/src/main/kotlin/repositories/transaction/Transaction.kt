package repositories.transaction


import repositories.SessionRepo

interface Transaction {
    val sessionRepo: SessionRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
