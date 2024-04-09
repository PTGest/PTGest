package pt.isel.leic.ptgest.repository.jdbi.transaction

import org.jdbi.v3.core.Handle
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.jdbi.user.JdbiUserRepo
import pt.isel.leic.ptgest.repository.transaction.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val userRepo: UserRepo = JdbiUserRepo(handle)

    override fun commit() {
        handle.commit()
    }

    override fun rollback() {
        handle.rollback()
    }
}
