package pt.isel.leic.ptgest.repository.jdbi.workout

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.domain.workout.ExerciseDetails
import pt.isel.leic.ptgest.repository.WorkoutRepo

class JdbiWorkoutRepo(private val handle: Handle) : WorkoutRepo {

    override fun createExercise(name: String, description: String?, category: ExerciseType, ref: String?): Int =
        handle.createUpdate(
            """
            insert into exercise (name, description, category, ref)
            values (:name, :description, :category::exercise_category, :ref)
            """
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "category" to category.name,
                    "ref" to ref
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getExerciseDetails(exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, category, ref
            from exercise
            where id = :id
            """
        )
            .bind("id", exerciseId)
            .mapTo<ExerciseDetails>()
            .firstOrNull()

    override fun createSet(name: String, notes: String?, type: SetType, details: String): Int =
        handle.createUpdate(
            """
            insert into set (name, notes, type, details)
            values (:name, :type::set_type, :notes, :details)
            """
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "details" to details,
                    "type" to type.name,
                    "notes" to notes
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun associateExerciseToSet(exerciseId: Int, setId: Int) {
        handle.createUpdate(
            """
            insert into set_exercise (set_id, exercise_id)
            values (:set_id, :exerciseId)
            """
        )
            .bindMap(
                mapOf(
                    "setId" to setId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }
}
