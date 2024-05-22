package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.repository.TraineeRepo
import java.util.UUID

class JdbiTraineeRepo(private val handle: Handle) : TraineeRepo {

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
