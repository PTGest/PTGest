package pt.isel.leic.ptgest.repository.jdbi.auth

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.auth.model.RefreshTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.repository.UserRepo
import java.util.*

class JdbiUserRepo(private val handle: Handle) : UserRepo {

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

    override fun createRefreshToken(userId: UUID, tokenHash: String, expirationDate: Date) {
        handle.createUpdate(
            """
                insert into refresh_token (token, user_id, expiration)
                values (:tokenHash, :userId, :expirationDate)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "tokenHash" to tokenHash,
                    "userId" to userId,
                    "expirationDate" to expirationDate
                )
            )
            .execute()
    }

    override fun getRefreshTokenDetails(tokenHash: String): RefreshTokenDetails? {
        return handle.createQuery(
            """
                select user_id, expiration from refresh_token
                where token = :token
            """.trimIndent()
        )
            .bind("token", tokenHash)
            .mapTo<RefreshTokenDetails>()
            .firstOrNull()
    }

    override fun removeRefreshToken(tokenHash: String) {
        handle.createUpdate(
            """
                delete from refresh_token
                where token = :token
            """.trimIndent()
        )
            .bind("token", tokenHash)
            .execute()
    }

    override fun getUserDetails(email: String): UserDetails? =
        handle.createQuery(
            """
                select *  from "user"
                where email = :email
            """.trimIndent()
        )
            .bind("email", email)
            .mapTo<UserDetails>()
            .firstOrNull()

    override fun getUserDetails(userId: UUID): UserDetails? =
        handle.createQuery(
            """
                select *  from "user"
                where id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<UserDetails>()
            .firstOrNull()
}
