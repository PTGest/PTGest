package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import repositories.SessionRepo
import repositories.jdbi.JdbiSessionRepo
import repositories.transaction.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {
    override val sessionRepo: SessionRepo = JdbiSessionRepo(handle)

    override fun commit() {
        handle.commit()
    }

    override fun rollback() {
        handle.rollback()
    }
}
