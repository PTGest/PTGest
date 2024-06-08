package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
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

    override fun associateTraineeToCompany(traineeId: UUID, companyId: UUID) {
        handle.createUpdate(
            """
                insert into company_trainee (company_id, trainee_id)
                values (:companyId, :traineeId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
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

    override fun getTraineeDetails(traineeId: UUID): TraineeDetails? =
        handle.createQuery(
            """
                select gender, birthdate, phone_number
                from trainee
                where trainee.id = :traineeId
            """.trimIndent()
        )
            .bind("traineeId", traineeId)
            .mapTo<TraineeDetails>()
            .firstOrNull()

    override fun getTrainerDetails(trainerId: UUID): TrainerDetails? =
        handle.createQuery(
            """
                select gender, phone_number
                from trainer 
                where trainer.id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<TrainerDetails>()
            .firstOrNull()
}
