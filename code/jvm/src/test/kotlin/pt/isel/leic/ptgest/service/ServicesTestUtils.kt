package pt.isel.leic.ptgest.service

import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.mockito.Mockito.mock
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.repository.UserRepo
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.auth.AuthService
import pt.isel.leic.ptgest.services.auth.JwtService

object MockRepos {
    val mockUserRepo: UserRepo = mock(UserRepo::class.java)
}

object MockServices {

    val mockTransaction = object : Transaction {
        override val userRepo: UserRepo = MockRepos.mockUserRepo

        override fun commit() {}
        override fun rollback() {}
    }

    private val mockTransactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R =
            (mockTransaction).let(block)

        override fun <R> runWithLevel(level: TransactionIsolationLevel, block: (Transaction) -> R): R =
            (mockTransaction).let(block)
    }

    fun buildMockJwtService(jwtSecret: JWTSecret): JwtService =
        JwtService(
            jwtSecret,
            mockTransactionManager
        )

    fun buildMockAuthServices(jwtService: JwtService, authDomain: AuthDomain): AuthService =
        AuthService(
            jwtService,
            authDomain,
            mockTransactionManager
        )
}
