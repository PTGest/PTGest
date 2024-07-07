package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.TrainerWorkout
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.repository.WorkoutRepo
import java.util.*

class JdbiWorkoutRepo(private val handle: Handle) : WorkoutRepo {

    override fun createWorkout(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>
    ): Int =
        handle.createUpdate(
            """
            insert into workout (name, description, muscle_group)
            values (:name, :description, :muscleGroup::muscle_group[])
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "muscleGroup" to muscleGroup.map { it.name }.toTypedArray()
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

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
        name: String?,
        muscleGroup: MuscleGroup?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerWorkout> {
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfw.workout_id is not null" else ""

        return handle.createQuery(
            """
            select id, name, description, muscle_group
                case when tfw.workout_id is not null then true else false end as is_favorite
            from workout w 
            join workout_trainer wt on wt.workout_id = w.id
            left join trainer_favorite_workout tfw on w.id = tfw.workout_id and tfw.trainer_id = :trainerId
            where wt.trainer_id = :trainerId $nameCondition $muscleGroupCondition $isFavoriteCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<TrainerWorkout>()
            .list()
    }

    override fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?, isFavorite: Boolean): Int {
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfw.workout_id is not null" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout w 
            join workout_trainer wt on wt.workout_id = w.id
            left join trainer_favorite_workout tfw on w.id = tfw.workout_id and tfw.trainer_id = :trainerId
            where wt.trainer_id = :trainerId $nameCondition $muscleGroupCondition $isFavoriteCondition
            limit :limit offset :skip
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

    override fun getWorkoutDetails(workoutId: Int): Workout? =
        handle.createQuery(
            """
            select id, name, description, muscle_group
            from workout
            where id = :workoutId
            """.trimIndent()
        )
            .bind("workoutId", workoutId)
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

    override fun isWorkoutOwner(trainerId: UUID, workoutId: Int): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from workout_trainer
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
}
