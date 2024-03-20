package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.leic.ptgest.repository.AuthRepo
import pt.isel.leic.ptgest.repository.jdbi.JdbiAuthRepo
import pt.isel.leic.ptgest.repository.transaction.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val authRepo: AuthRepo = JdbiAuthRepo(handle)

    override fun commit() {
        handle.commit()
    }

    override fun rollback() {
        handle.rollback()
    }
}