package pt.isel.leic.ptgest.repository.jdbi.workout

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.repository.WorkoutRepo

class JdbiWorkoutRepo(private val handle: Handle) : WorkoutRepo {

    override fun createExercise(
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int =
        handle.createUpdate(
            """
            insert into exercise (name, description, category, type, ref)
            values (:name, :description, :muscleGroup::muscle_group, :type::exercise_type, :ref)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "muscleGroup" to muscleGroup.name,
                    "type" to exerciseType.name,
                    "ref" to ref
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun createSet(name: String, notes: String?, type: SetType): Int =
        handle.createUpdate(
            """
            insert into set (name, notes, type)
            values (:name, :type::set_type, :notes)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "type" to type.name,
                    "notes" to notes
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String) {
        handle.createUpdate(
            """
            insert into set_exercise (order_id, set_id, exercise_id, details)
            values (:orderId, :set_id, :exerciseId, :details)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "orderId" to orderId,
                    "setId" to setId,
                    "exerciseId" to exerciseId,
                    "details" to details
                )
            )
            .execute()
    }
}
