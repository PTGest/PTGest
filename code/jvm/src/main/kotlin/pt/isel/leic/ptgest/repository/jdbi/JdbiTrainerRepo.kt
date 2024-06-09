package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.*

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {

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

    override fun getCompanyAssignedTrainer(trainerId: UUID): UUID? =
        handle.createQuery(
            """
            select company_id
            from company_trainer
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<UUID>()
            .firstOrNull()

    override fun associateTrainerToReport(trainerId: UUID, reportId: Int) {
        handle.createUpdate(
            """
            insert into report_trainer (report_id, trainer_id)
            values (:reportId, :trainerId)
            """
        )
            .bindMap(
                mapOf(
                    "reportId" to reportId,
                    "trainerId" to trainerId
                )
            )
            .execute()
    }

    override fun getTraineeIdByName(name: String): UUID? {
        return handle.createQuery(
            """
            select id
            from "user" join trainee on id = trainee_id
            where name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<UUID>()
            .firstOrNull()
    }

    override fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            insert into exercise_trainer (trainer_id, exercise_id)
            values (:trainerId, :exerciseId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }
}
