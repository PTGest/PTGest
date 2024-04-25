package pt.isel.leic.ptgest.repository.jdbi.user

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.user.TraineeDetails
import pt.isel.leic.ptgest.domain.user.TrainerDetails
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
                values (:name, :email, :passwordHash, :role::role)
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

    override fun createTrainer(id: UUID, gender: Gender, phoneNumber: String?) {
        handle.createUpdate(
            """
                insert into trainer (id, gender, phone_number)
                values (:id, :gender::gender, :phoneNumber)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to id,
                    "gender" to gender.name,
                    "phoneNumber" to phoneNumber
                )
            )
            .execute()
    }

    override fun createCompanyTrainer(companyId: UUID, trainerId: UUID, capacity: Int) {
        handle.createUpdate(
            """
                insert into company_trainer (company_id, trainer_id, capacity)
                values (:companyId, :trainerId, :capacity)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "trainerId" to trainerId,
                    "capacity" to capacity
                )
            )
            .execute()
    }

    override fun createTrainee(
        userId: UUID,
        birthdate: Date,
        gender: Gender,
        phoneNumber: String?
    ) {
        handle.createUpdate(
            """
                insert into trainee (id, gender, birthdate, phone_number)
                values (:id, :gender, :birthdate, :phoneNumber)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to userId,
                    "gender" to gender,
                    "birthdate" to birthdate,
                    "phoneNumber" to phoneNumber
                )
            )
    }

    override fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID) {
        handle.createUpdate(
            """
                insert into trainer_trainer (trainer_id, trainee_id)
                values (:trainerId, :traineeId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "traineeId" to traineeId
                )
            )
            .execute()
    }

    override fun resetPassword(userId: UUID, newPasswordHash: String) {
        handle.createUpdate(
            """
                update "user"
                set password_hash = :newPasswordHash
                where id = :userId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "newPasswordHash" to newPasswordHash,
                    "userId" to userId
                )
            )
            .execute()
    }

    override fun createToken(tokenHash: String, userId: UUID, expirationDate: Date) {
        handle.createUpdate(
            """
                insert into token (token_hash, user_id, expiration)
                values (:tokenHash, :userId, :expiration)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "userId" to userId,
                    "tokenHash" to tokenHash,
                    "expiration" to expirationDate
                )
            )
            .execute()
    }

    override fun removeToken(tokenHash: String) {
        handle.createUpdate(
            """
                delete from token
                where token_hash = :token
            """.trimIndent()
        )
            .bind("token", tokenHash)
            .execute()
    }

    override fun createRefreshToken(tokenHash: String) {
        handle.createUpdate(
            """
                insert into refresh_token (token_hash)
                values (:token)
            """.trimIndent()
        )
            .bind("token", tokenHash)
            .execute()
    }

    override fun getRefreshTokenDetails(tokenHash: String): TokenDetails? {
        return handle.createQuery(
            """
                select rt.token_hash, user_id, expiration
                from refresh_token rt join token t on rt.token_hash = t.token_hash
                where rt.token_hash = :token_hash
            """.trimIndent()
        )
            .bind("token_hash", tokenHash)
            .mapTo<TokenDetails>()
            .firstOrNull()
    }

    override fun createPasswordResetToken(tokenHash: String) {
        handle.createUpdate(
            """
                insert into password_reset_token (token_hash)
                values (:token)
            """.trimIndent()
        )
            .bind("token", tokenHash)
            .execute()
    }

    override fun removeOldPasswordResetTokens(userId: UUID) {
        handle.createUpdate(
            """
                delete from token 
                using password_reset_token prt
                where token.token_hash = prt.token_hash and token.user_id = :userId;
            """.trimIndent()
        )
            .bind("userId", userId)
            .execute()
    }

    override fun getPasswordResetToken(tokenHash: String) =
        handle.createQuery(
            """
                select prt.token_hash, user_id, expiration
                from password_reset_token prt join token t on prt.token_hash = t.token_hash
                where prt.token_hash = :tokenHash
            """.trimIndent()
        )
            .bind("tokenHash", tokenHash)
            .mapTo<TokenDetails>()
            .firstOrNull()

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

    override fun getTraineeDetails(userId: UUID): TraineeDetails? =
        handle.createQuery(
            """
                select name, email, gender, birthdate, phone_number
                from trainee join "user" on trainee.id = "user".id
                where id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<TraineeDetails>()
            .firstOrNull()

    override fun getTrainerDetails(userId: UUID): TrainerDetails? =
        handle.createQuery(
            """
                select name, email, gender, phone_number
                from trainer join "user" on trainer.id = "user".id
                where id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<TrainerDetails>()
            .firstOrNull()
}
