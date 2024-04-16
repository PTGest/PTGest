package pt.isel.leic.ptgest.repository.transaction

import pt.isel.leic.ptgest.repository.CompanyRepo
import pt.isel.leic.ptgest.repository.UserRepo

interface Transaction {
    val userRepo: UserRepo
    val companyRepo: CompanyRepo

    /**
     * Explicitly commits the transaction.
     */
    fun commit()

    /**
     * Explicitly rolls back the transaction.
     */
    fun rollback()
}
