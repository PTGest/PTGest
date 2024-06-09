package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.trainee.model.TraineeDetails
import pt.isel.leic.ptgest.repository.TraineeRepo
import java.util.*

class JdbiTraineeRepo(private val handle: Handle) : TraineeRepo {

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

    override fun getTrainerAssigned(traineeId: UUID): UUID? =
        handle.createQuery(
            """
                select trainer_id
                from trainer_trainee
                where trainee_id = :traineeId
            """.trimIndent()
        )
            .bind("traineeId", traineeId)
            .mapTo<UUID>()
            .firstOrNull()
}
