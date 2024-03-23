package pt.isel.leic.ptgest.repository.jdbi.auth

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.AuthRepo
import java.time.LocalDate
import java.util.*

class JdbiAuthRepo(private val handle: Handle) : AuthRepo {
    override fun createUser(
        name: String,
        email: String,
        passwordHash: String,
        role: Role
    ): UUID {
        return handle.createUpdate(
            """
                insert into "user" (name, email, password_hash, role)
                values (:name, :email, :passwordHash, :role)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "passwordHash" to passwordHash,
                    "role" to role.name
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

    override fun getUserDetails(email: String): UserDetails? =
        handle.createQuery(
            """
                select id, name, email, password_hash  from "user"
                where email = :email
            """.trimIndent()
        )
            .bind("email", email)
            .mapTo<UserDetails>()
            .firstOrNull()


    override fun createToken(
        userId: UUID,
        tokenHash: String,
        creationDate: LocalDate,
        expirationDate: LocalDate
    ) {
        handle.createUpdate(
            """
                insert into token (token_hash, user_id, creation_date, expiration_date)
                values (:tokenHash, :userId, :creationDate, :expirationDate)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "tokenHash" to tokenHash,
                    "userId" to userId,
                    "creationDate" to creationDate,
                    "expirationDate" to expirationDate
                )
            )
            .execute()
    }


    override fun getToken(tokenHash: String): TokenDetails? =
        handle.createQuery(
            """
                select token_hash, user_id, creation_date, expiration_date, role
                from token join dev."user" u on u.id = token.user_id
                where token_hash = :tokenHash
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
            .bindMap(
                mapOf(
                    "tokenHash" to tokenHash,
                    "expirationDate" to expirationDate
                )
            )
            .execute()
    }
}