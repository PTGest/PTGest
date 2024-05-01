package pt.isel.leic.ptgest.repository.jdbi.trainer

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.UUID

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

    override fun getLastSetNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)$') as int) as set_number
            from set s join dev.set_trainer st on s.id = st.set_id
            where name like 'Set #%' and st.trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)$') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()
}
