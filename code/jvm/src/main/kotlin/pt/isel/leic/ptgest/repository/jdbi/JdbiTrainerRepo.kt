package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.UUID

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {

    override fun getCompanyAssignedTrainer(trainerId: UUID): UUID =
        handle.createQuery(
            """
            select company_id
            from company_trainer
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<UUID>()
            .one()

    override fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select name, description, muscle_group, type, ref
            from exercise e join exercise_trainer et on e.id = et.exercise_id
            where id = :id and trainer_id = :trainer_id
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to exerciseId,
                    "trainer_id" to trainerId
                )
            )
            .mapTo<ExerciseDetails>()
            .firstOrNull()

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

    override fun getLastSetNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)$') as int) as set_number
            from set
            where name like 'Set #%' and trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)$') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()

    override fun getLastWorkoutNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)${'$'}') as int) as workout_number
            from workout
            where name like 'Workout #%' and trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)${'$'}') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()

    override fun getSet(trainerId: UUID, setId: Int): Set? =
        handle.createQuery(
            """
            select name, notes, type
            from set
            where id = :setId and trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "setId" to setId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Set>()
            .firstOrNull()

    override fun getSet(setId: Int): Set =
        handle.createQuery(
            """
            select name, notes, type
            from set
            where id = :setId
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<Set>()
            .one()

    override fun getSetExercises(setId: Int): List<Exercise> =
        handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.type, se.details
            from exercise e join set_exercise se on e.id = se.exercise_id
            where set_id = :setId
            order by se.order_id
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<Exercise>()
            .list()

    override fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout? =
        handle.createQuery(
            """
            select name, description, category
            from workout
            where id = :workoutId and trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "workoutId" to workoutId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Workout>()
            .firstOrNull()

    override fun getWorkoutSetIds(workoutId: Int): List<Int> =
        handle.createQuery(
            """
            select set_id
            from workout_set
            where workout_id = :workoutId
            order by order_id desc
            """.trimIndent()
        )
            .bind("workoutId", workoutId)
            .mapTo<Int>()
            .list()
}