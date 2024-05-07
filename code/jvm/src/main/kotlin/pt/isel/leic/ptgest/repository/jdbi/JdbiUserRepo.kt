package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.repository.UserRepo
import java.util.UUID

class JdbiUserRepo(private val handle: Handle) : UserRepo {

    override fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID) {
        handle.createUpdate(
            """
                insert into trainer_trainee (trainer_id, trainee_id)
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
                where trainee.id = :userId
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
                where trainer.id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<TrainerDetails>()
            .firstOrNull()
}
