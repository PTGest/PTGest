package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.exercise.model.TrainerExercise
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
    ): Int =
        handle.createUpdate(
            """
            insert into exercise (name, description, muscle_group, modality, ref)
            values (:name, :description, :muscleGroup::muscle_group[], :modality::modality, :ref)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "name" to name,
                    "description" to description,
                    "modality" to modality.name,
                    "ref" to ref,
                    "muscleGroup" to muscleGroup.map { it.name }.toTypedArray()
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getCompanyExercises(
        companyId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        skip: Int,
        limit: Int?
    ): List<Exercise> {
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and modality = :modality::modality" } ?: ""

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
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and modality = :modality::modality" } ?: ""

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

    override fun getCompanyTrainerExercises(
        companyId: UUID,
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerExercise> {
        val nameCondition = name?.let { "and e.name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(e.muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and e.modality = :modality::modality" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfe.exercise_id is not null" else ""

        return handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.modality,
                   case when tfe.exercise_id is not null then true else false end as is_favorite
            from exercise_company ec
            join exercise e on ec.exercise_id = e.id
            left join trainer_favorite_exercise tfe on e.id = tfe.exercise_id and tfe.trainer_id = :trainerId
            where ec.company_id = :companyId $nameCondition $muscleGroupCondition $modalityCondition $isFavoriteCondition
            limit :limit offset :skip;
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<TrainerExercise>()
            .list()
    }

    override fun getTotalCompanyTrainerExercises(
        companyId: UUID,
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean
    ): Int {
        val nameCondition = name?.let { "and e.name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(e.muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and e.modality = :modality::modality" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfe.exercise_id is not null" else ""

        return handle.createQuery(
            """
            select count(*)
            from exercise_company ec
            join exercise e on ec.exercise_id = e.id
            left join trainer_favorite_exercise tfe on e.id = tfe.exercise_id and tfe.trainer_id = :trainerId
            where ec.company_id = :companyId $nameCondition $muscleGroupCondition $modalityCondition $isFavoriteCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "trainerId" to trainerId,
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
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerExercise> {
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and modality = :modality::modality" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfe.exercise_id is not null" else ""

        return handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.modality,
                case when tfe.exercise_id is not null then true else false end as is_favorite
            from exercise_trainer ec
            join exercise e on ec.exercise_id = e.id
            left join trainer_favorite_exercise tfe on ec.trainer_id = tfe.trainer_id and e.id = tfe.exercise_id
            where ec.trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition $isFavoriteCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<TrainerExercise>()
            .list()
    }

    override fun getTotalTrainerExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean
    ): Int {
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val muscleGroupCondition = muscleGroup?.let { "and :muscleGroup::muscle_group = any(muscle_group)" } ?: ""
        val modalityCondition = modality?.let { "and modality = :modality::modality" } ?: ""
        val isFavoriteCondition = if (isFavorite) "and tfe.exercise_id is not null" else ""

        return handle.createQuery(
            """
            select count(*)
            from exercise_trainer ec
            join exercise e on ec.exercise_id = e.id
            left join trainer_favorite_exercise tfe on ec.trainer_id = tfe.trainer_id and e.id = tfe.exercise_id
            where ec.trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition $isFavoriteCondition
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

    override fun isFavoriteExercise(trainerId: UUID, exerciseId: Int): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from trainer_favorite_exercise
                where trainer_id = :trainerId and exercise_id = :exerciseId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "exerciseId" to exerciseId
                )
            )
            .mapTo<Boolean>()
            .one()

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

    override fun getExerciseDetails(exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, muscle_group, modality, ref
            from exercise
            where id = :id
            """.trimIndent()
        )
            .bind("id", exerciseId)
            .mapTo<ExerciseDetails>()
            .firstOrNull()
}
