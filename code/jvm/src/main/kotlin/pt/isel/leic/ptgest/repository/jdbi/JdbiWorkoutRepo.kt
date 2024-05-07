package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.repository.WorkoutRepo
import java.util.UUID

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

    override fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int =
        handle.createUpdate(
            """
            insert into set (trainer_id, name, notes, type)
            values (:trainerId, :name, :type::set_type, :notes)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
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

    override fun createWorkout(trainerId: UUID, name: String, description: String?, category: MuscleGroup): Int =
        handle.createUpdate(
            """
            insert into workout (trainer_id, name, description, category)
            values (:trainerId, :name, :description, :category::muscle_group)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to name,
                    "description" to description,
                    "category" to category.name
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun associateSetToWorkout(orderId: Int, setId: Int, workoutId: Int) {
        handle.createUpdate(
            """
            insert into workout_set (order_id, workout_id, set_id)
            values (:orderId, :workoutId, :setId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "orderId" to orderId,
                    "workoutId" to workoutId,
                    "setId" to setId
                )
            )
            .execute()
    }
}
