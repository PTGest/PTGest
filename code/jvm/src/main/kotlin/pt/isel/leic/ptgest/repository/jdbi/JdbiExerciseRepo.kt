package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.repository.ExerciseRepo
import java.util.*

class JdbiExerciseRepo(private val handle: Handle) : ExerciseRepo {

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

    override fun getCompanyExercises(
        companyId: UUID,
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
            from exercise_company ec join exercise e on ec.exercise_id = e.id
            where company_id = :companyId $nameCondition $muscleGroupCondition $modalityCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
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

    override fun getTotalCompanyExercises(
        companyId: UUID,
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
            from exercise_company ec join exercise e on ec.exercise_id = e.id
            where company_id = :companyId $nameCondition $muscleGroupCondition $modalityCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getCompanyExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, muscle_group, modality, ref
            from exercise e join exercise_company et on e.id = et.exercise_id
            where id = :id and company_id = :companyId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to exerciseId,
                    "companyId" to companyId
                )
            )
            .mapTo<ExerciseDetails>()
            .firstOrNull()

    override fun getTrainerExercises(
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

    override fun getTotalTrainerExercises(
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

    override fun favoriteExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_exercise (trainer_id, exercise_id)
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

    override fun unfavoriteExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_exercise
            where trainer_id = :trainerId and exercise_id = :exerciseId
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

    override fun getFavoriteExercises(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select exercise_id
            from trainer_favorite_exercise
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun getFavoriteExercises(
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
            select e.id, e.name, e.muscle_group, e.modality
            from exercise e join trainer_favorite_exercise tfe on e.id = tfe.exercise_id
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

    override fun getTotalFavoriteExercises(
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
            from exercise e join trainer_favorite_exercise tfe on e.id = tfe.exercise_id
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

    override fun getTrainerExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails? =
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
}
