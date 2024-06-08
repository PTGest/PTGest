package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.repository.CompanyRepo
import java.util.UUID

class JdbiCompanyRepo(private val handle: Handle) : CompanyRepo {

    override fun getCompanyTrainers(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): List<Trainer> {
        val genderCondition = if (gender != null) "and t.gender = :gender" else ""
        val nameCondition = if (name != null) "and name like :name" else ""
        val excludeCondition = if (excludeTraineeTrainer != null) {
            "where t_t.trainee_id != :excludeTraineeTrainer"
        } else {
            ""
        }

        return handle.createQuery(
            """
            select id, name, gender, assigned_trainees, capacity
            from company_trainer c_pt join (
                select u_d.id, u_d.name, u_d.gender, count(t_t.trainee_id) as assigned_trainees
                from (
                    select u.id as id, name, gender
                    from "user" u join trainer pt on u.id = pt.id
                ) u_d left join trainer_trainee t_t on u_d.id = t_t.trainer_id
                $excludeCondition
                group by u_d.id, u_d.name, u_d.gender
            ) ptd on c_pt.trainer_id = ptd.id
            where company_id = :companyId $genderCondition $nameCondition
            order by (capacity - assigned_trainees) ${availability.name.lowercase()}
            limit :limit offset :skip;
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "gender" to gender,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%"
                )
            )
            .mapTo<Trainer>()
            .list()
    }

    override fun getTotalCompanyTrainers(
        companyId: UUID,
        gender: Gender?,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): Int {
        val genderCondition = if (gender != null) "and t.gender = :gender" else ""
        val nameCondition = if (name != null) "and name like :name" else ""
        val excludeCondition = if (excludeTraineeTrainer != null) {
            "where t_t.trainee_id != :excludeTraineeTrainer"
        } else {
            ""
        }

        return handle.createQuery(
            """
            select count(*)
            from company_trainer c_pt join (
                select u_d.id, u_d.name, u_d.gender, count(t_t.trainee_id) as total_trainees
                from (
                    select u.id as id, name, gender
                    from "user" u join trainer pt on u.id = pt.id
                ) u_d left join trainer_trainee t_t on u_d.id = t_t.trainer_id
                $excludeCondition
                group by u_d.id, u_d.name, u_d.gender
            ) ptd on c_pt.trainer_id = ptd.id
            where company_id = :companyId $genderCondition $nameCondition $excludeCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "gender" to gender,
                    "name" to "%$name%",
                    "excludeTraineeTrainer" to excludeTraineeTrainer
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getCompanyTrainees(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee> {
        val genderCondition = if (gender != null) "and gender = :gender" else ""
        val nameCondition = if (name != null) "and ut.name like :name" else ""

        return handle.createQuery(
            """
            select t.id as trainee_id, ut.name as trainee_name, t.gender, upt.id as trainer_id, upt.name as trainer_name
            from trainee t
            join "user" ut on t.id = ut.id
            left join trainer_trainee tt on t.id = tt.trainee_id
            join company_trainee ct on t.id = ct.trainee_id
            left join "user" upt on tt.trainer_id = upt.id
            where ct.company_id = :companyId $genderCondition $nameCondition
            limit :limit offset :skip;
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "skip" to skip,
                    "limit" to limit,
                    "gender" to gender,
                    "name" to "%$name%"
                )
            )
            .mapTo<Trainee>()
            .list()
    }

    override fun getTotalCompanyTrainees(companyId: UUID, gender: Gender?, name: String?): Int {
        val genderCondition = if (gender != null) "and gender = :gender" else ""
        val nameCondition = if (name != null) "and ut.name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from trainee t
            join "user" ut on t.id = ut.id
            left join trainer_trainee tt on t.id = tt.trainee_id
            join company_trainee ct on t.id = ct.trainee_id
            left join "user" upt on tt.trainer_id = upt.id
            where ct.company_id = :companyId $genderCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "gender" to gender,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getCompanyTrainer(trainerId: UUID, companyId: UUID): Trainer? =
        handle.createQuery(
            """
            select id, name, gender, capacity, assigned_trainees
            from company_trainer c_pt join (
                select id, name, gender, count(t_t.trainee_id) as assigned_trainees
                from (
                    select u.id, name, gender
                    from "user" u join trainer pt on u.id = pt.id
                ) u_d left join trainer_trainee t_t on u_d.id = t_t.trainer_id
                group by id, name, gender
            ) ptd on c_pt.trainer_id = ptd.id
            where company_id = :companyId and  trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Trainer>()
            .firstOrNull()

    override fun assignTrainerToTrainee(trainerId: UUID, traineeId: UUID) {
        handle.createUpdate(
            """
                insert into trainer_trainee(trainer_id, trainee_id)
                values (:trainerId, :traineeId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "traineeId" to traineeId
                )
            )
            .execute()
    }

    override fun reassignTrainer(trainerId: UUID, traineeId: UUID) {
        handle.createUpdate(
            """
                update trainer_trainee
                set trainer_id = :trainerId
                where trainee_id = :traineeId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "traineeId" to traineeId
                )
            )
            .execute()
    }

    override fun updateTrainerCapacity(companyId: UUID, trainerId: UUID, capacity: Int) {
        handle.createUpdate(
            """
                update company_trainer
                set capacity = :capacity
                where company_id = :companyId and trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "trainerId" to trainerId,
                    "capacity" to capacity
                )
            )
            .execute()
    }

    override fun associateCompanyToExercise(companyId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
                insert into exercise_company(company_id, exercise_id)
                values (:companyId, :exerciseId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to companyId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }

    override fun getExercises(
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

    override fun getTotalExercises(
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

    override fun getExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails? =
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
}
