package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.set.model.Set
import pt.isel.leic.ptgest.domain.set.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSet
import pt.isel.leic.ptgest.repository.SetRepo
import java.util.*

class JdbiSetRepo(private val handle: Handle) : SetRepo {

    override fun createSet(name: String, notes: String?, type: SetType): Int =
        handle.createUpdate(
            """
            insert into set (name, type, notes)
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

    override fun getLastSetNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)$') as int) as set_number
            from set s
            join set_trainer st on st.set_id = s.id 
            where name like 'Set #%' and trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)$') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()

    override fun getSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set> {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
                select id, name, notes, type
                from set s join set_trainer st on s.id = st.set_id
                where st.trainer_id = :trainerId
                $typeCondition
                $nameCondition
                limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Set>()
            .list()
    }

    override fun getTotalSets(trainerId: UUID, type: SetType?, name: String?): Int {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from set s join set_trainer st on s.id = st.set_id
            where st.trainer_id = :trainerId $typeCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getFavoriteSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set> {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select s.id, s.name, s.notes, s.type
            from set s join trainer_favorite_set tfs on s.id = tfs.set_id
            where trainer_id = :trainerId $typeCondition $nameCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Set>()
            .list()
    }

    override fun getTotalFavoriteSets(trainerId: UUID, type: SetType?, name: String?): Int {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from set s join trainer_favorite_set tfs on s.id = tfs.set_id
            where trainer_id = :trainerId $typeCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun isSetFavorite(trainerId: UUID, setId: Int): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from trainer_favorite_set
                where trainer_id = :trainerId and set_id = :setId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "setId" to setId
                )
            )
            .mapTo<Boolean>()
            .one()

    override fun getSet(trainerId: UUID, setId: Int): Set? =
        handle.createQuery(
            """
            select id, name, notes, type
            from set s
            join set_trainer st on st.set_id = s.id
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

    override fun getWorkoutSet(workoutId: Int, setId: Int): WorkoutSet? = handle.createQuery(
        """
            select s.id, ws.order_id, s.name, s.notes, s.type
            from workout_set ws join set s on ws.set_id = s.id
            where ws.workout_id = :workoutId and ws.set_id = :setId
        """.trimIndent()
    )
        .bindMap(
            mapOf(
                "workoutId" to workoutId,
                "setId" to setId
            )
        )
        .mapTo<WorkoutSet>()
        .firstOrNull()

    override fun getSetExercises(setId: Int): List<SetExerciseDetails> =
        handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.modality, se.details
            from exercise e join set_exercise se on e.id = se.exercise_id 
            where set_id = :setId
            order by se.order_id
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<SetExerciseDetails>()
            .list()

    override fun getFavoriteSetsByTrainerId(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select set_id
            from trainer_favorite_set
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun favoriteSet(trainerId: UUID, setId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_set (trainer_id, set_id)
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

    override fun unfavoriteSet(trainerId: UUID, setId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_set
            where trainer_id = :trainerId and set_id = :setId
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

    override fun getSetByExercises(exerciseIds: List<Int>): List<Int> =
        handle.createQuery(
            """
            select set_id
            from set_exercise
            group by set_id
            having array_agg(exercise_id ORDER BY order_id) = :exercises::integer[]
            """.trimIndent()
        )
            .bind("exercisesArray", exerciseIds.toTypedArray())
            .mapTo<Int>()
            .list()

    override fun validateSetExerciseDetails(setId: Int, exerciseId: Int, details: String): Boolean {
        return handle.createQuery(
            """
            select exists(
                select 1
                from set_exercise
                where set_id = :setId and exercise_id = :exerciseId and (
                    select jsonb_object_agg(key, value)
                    from jsonb_each_text(details)
                ) = (
                    select jsonb_object_agg(key, value)
                    from jsonb_each_text(:details::jsonb)
                )
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "setId" to setId,
                    "exerciseId" to exerciseId,
                    "details" to details
                )
            )
            .mapTo<Boolean>()
            .one()
    }
}
