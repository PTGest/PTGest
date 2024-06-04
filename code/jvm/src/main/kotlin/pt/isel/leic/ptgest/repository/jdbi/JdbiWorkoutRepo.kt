package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.repository.WorkoutRepo
import java.util.UUID

class JdbiWorkoutRepo(private val handle: Handle) : WorkoutRepo {

    override fun createExercise(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int {
        val muscleGroupArray = muscleGroup.joinToString(",") { "'${it.name}'::muscle_group" }

        return handle.createUpdate(
            """
            insert into exercise (name, description, muscle_group, modality, ref)
            values (:name, :description, ARRAY[$muscleGroupArray], :modality::modality, :ref)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "modality" to modality.name,
                    "ref" to ref
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

    override fun getExerciseDetails(exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, muscle_group, type, ref
            from exercise
            where id = :id
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to exerciseId
                )
            )
            .mapTo<ExerciseDetails>()
            .firstOrNull()

    override fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int =
        handle.createUpdate(
            """
            insert into set (trainer_id, name, type, notes)
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
            values (:orderId, :setId, :exerciseId, cast(:details as jsonb))
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

    override fun createWorkout(
        trainerId: UUID,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>
    ): Int {
        val muscleGroupArray = muscleGroup.joinToString(",") { "'${it.name}'::muscle_group" }

        return handle.createUpdate(
            """
            insert into workout (trainer_id, name, description, category)
            values (:trainerId, :name, :description, ARRAY[$muscleGroupArray])
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to name,
                    "description" to description
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

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
