package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.repository.AuthRepo
import java.time.LocalDate
import java.util.*

class JdbiAuthRepo(private val handle: Handle) : AuthRepo {
    override fun createUser(name: String, email: String, passwordHash: String): UUID {
        return handle.createUpdate(
            """
                insert into "user" (name, email, password_hash)
                values (:name, :email, :passwordHash)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "passwordHash" to passwordHash
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<UUID>()
            .one()
    }

    override fun createCompany(id: UUID) {
        handle.createUpdate(
            """
                insert into company (id)
                values (:id)
            """.trimIndent()
        )
            .bind("id", id)
            .execute()
    }

    override fun createIndependentTrainer(id: UUID, gender: Gender, phoneNumber: String?) {
        handle.createUpdate(
            """
                insert into personal_trainer (id, gender, contact)
                values (:id, :gender, :phoneNumber)
                """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to id,
                    "gender" to gender.identifier,
                    "phoneNumber" to phoneNumber
                )
            )
            .execute()
    }


    override fun getToken(tokenHash: String): TokenDetails? =
        handle.createQuery(
            """
                select * from token
                where hash = :tokenHash
            """.trimIndent()
        )
            .bind("tokenHash", tokenHash)
            .mapTo<TokenDetails>()
            .firstOrNull()

    override fun revokeToken(tokenHash: String) {
        handle.createUpdate(
            """
                delete from token
                where hash = :tokenHash
            """.trimIndent()
        )
            .bind("tokenHash", tokenHash)
            .execute()
    }

    override fun updateExpirationDate(tokenHash: String, expirationDate: LocalDate) {
        handle.createUpdate(
            """
                update token
                set expiration_date = :expirationDate
                where hash = :tokenHash
            """.trimIndent()
        )
            .bind("tokenHash", tokenHash)
            .bind("expirationDate", expirationDate)
            .execute()
    }
}