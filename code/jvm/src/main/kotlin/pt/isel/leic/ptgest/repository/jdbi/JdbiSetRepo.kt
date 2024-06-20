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

    override fun getSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set> {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select id, name, notes, type
            from set
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

    override fun getTotalSets(trainerId: UUID, type: SetType?, name: String?): Int {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from set
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

    override fun getWorkoutSet(workoutId: Int, setId: Int): WorkoutSet? {
        TODO("Not yet implemented")
    }

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
}
