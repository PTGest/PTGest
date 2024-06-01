package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.model.SessionType
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.*

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {

    override fun getCompanyAssignedTrainer(trainerId: UUID): UUID? =
        handle.createQuery(
            """
            select company_id
            from company_trainer
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<UUID>()
            .firstOrNull()

    override fun getExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select id, name, muscle_group, modality
            from exercise_trainer ec join exercise e on ec.exercise_id = e.id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Exercise>()
            .list()
    }

    override fun getTotalExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select count(*)
            from exercise_trainer ec join exercise e on ec.exercise_id = e.id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, muscle_group, modality, ref
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
            select id, name, notes, type
            from set
            where id = :setId
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<Set>()
            .one()

    override fun getSetExercises(setId: Int): List<SetExerciseDetails> =
        handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.type, se.details
            from exercise e join set_exercise se on e.id = se.exercise_id
            where set_id = :setId
            order by se.order_id
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<SetExerciseDetails>()
            .list()

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
            from workout
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

    override fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout
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

    override fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout? =
        handle.createQuery(
            """
            select id, name, description, muscle_group
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

    override fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int =
            handle.createUpdate(
                """
            insert into session (trainee_id, trainer_id, workout_id, begin_date, end_date, type, notes)
            values (:traineeId, :trainerId, :workoutId, :beginDate, :endDate, :type, :notes)
            returning id
            """
            )
                .bindMap(
                    mapOf(
                        "traineeId" to traineeId,
                        "trainerId" to trainerId,
                        "workoutId" to workoutId,
                        "beginDate" to beginDate,
                        "endDate" to endDate,
                        "type" to type.name,
                        "notes" to notes
                    )
                )
                .executeAndReturnGeneratedKeys("id")
                .mapTo<Int>()
                .one()

}
