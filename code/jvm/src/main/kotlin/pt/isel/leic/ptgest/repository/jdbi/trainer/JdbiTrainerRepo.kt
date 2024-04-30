package pt.isel.leic.ptgest.repository.jdbi.trainer

import org.jdbi.v3.core.Handle
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.*

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {

    override fun associateTrainerToExercise(exerciseId: Int, trainerId: UUID) {
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

    override fun associateTrainerToSet(setId: Int, trainerId: UUID) {
        handle.createUpdate(
            """
            insert into set_trainer (trainer_id, set_id)
            values (:trainerId, :setId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "setId" to setId
                )
            )
            .execute()
    }
}
