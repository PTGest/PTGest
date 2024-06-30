package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.repository.WorkoutRepo
import java.util.*

class JdbiWorkoutRepo(private val handle: Handle) : WorkoutRepo {

    override fun createWorkout(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>
    ): Int {
        val muscleGroupArray = muscleGroup.joinToString(",") { "'${it.name}'::muscle_group" }

        return handle.createUpdate(
            """
            insert into workout (name, description, muscle_group)
            values (:name, :description, ARRAY[$muscleGroupArray])
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

    override fun getWorkoutBySets(sets: List<Int>): Int? =
        handle.createQuery(
            """
            select workout_id
            from workout_set
            group by workout_id
            having array_agg(set_id order by order_id) = :sets::integer[]
            """.trimIndent()
        )
            .bind("sets", sets.toTypedArray())
            .mapTo<Int>()
            .firstOrNull()

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

    //  Workout related methods
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

    override fun getWorkouts(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?
    ): List<Workout> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select id, name, description, muscle_group
            from workout w join workout_trainer wt on wt.workout_id = w.id
            where wt.trainer_id = :trainerId $nameCondition $muscleGroupCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Workout>()
            .list()
    }

    override fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout w join workout_trainer wt on wt.workout_id = w.id
            where wt.trainer_id = :trainerId $nameCondition $muscleGroupCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getFavoriteWorkouts(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?
    ): List<Workout> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select w.id, w.name, w.description, w.muscle_group
            from workout w join trainer_favorite_workout tfw on w.id = tfw.workout_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Workout>()
            .list()
    }

    override fun getTotalFavoriteWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout w join trainer_favorite_workout tfw on w.id = tfw.workout_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun isWorkoutFavorite(trainerId: UUID, workoutId: Int): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from trainer_favorite_workout
                where trainer_id = :trainerId and workout_id = :workoutId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "workoutId" to workoutId
                )
            )
            .mapTo<Boolean>()
            .one()

    override fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout? =
        handle.createQuery(
            """
            select id, name, description, muscle_group
            from workout w join workout_trainer wt on wt.workout_id = w.id
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

    override fun getFavoriteWorkoutsByTrainerId(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select workout_id
            from trainer_favorite_workout
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun favoriteWorkout(trainerId: UUID, workoutId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_workout (trainer_id, workout_id)
            values (:trainerId, :workoutId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "workoutId" to workoutId
                )
            )
            .execute()
    }

    override fun unfavoriteWorkout(trainerId: UUID, workoutId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_workout
            where trainer_id = :trainerId and workout_id = :workoutId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "workoutId" to workoutId
                )
            )
            .execute()
    }
}
