package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.repository.CompanyRepo
import java.util.*

class JdbiCompanyRepo(private val handle: Handle) : CompanyRepo {

    override fun getTrainers(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): List<Trainer> {
        val genderCondition = gender?.let { "and t.gender = :gender" } ?: ""
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val excludeCondition = excludeTraineeTrainer?.let { "where t_t.trainee_id != :excludeTraineeTrainer" } ?: ""

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
            order by (capacity - assigned_trainees) ${availability.name}
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

    override fun getTotalTrainers(
        companyId: UUID,
        gender: Gender?,
        name: String?,
        excludeTraineeTrainer: UUID?
    ): Int {
        val genderCondition = gender?.let { "and t.gender = :gender" } ?: ""
        val nameCondition = name?.let { "and name like :name" } ?: ""
        val excludeCondition = excludeTraineeTrainer?.let { "where t_t.trainee_id != :excludeTraineeTrainer" } ?: ""

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

    override fun getTrainees(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee> {
        val genderCondition = gender?.let { "and gender = :gender" } ?: ""
        val nameCondition = name?.let { "and ut.name like :name" } ?: ""

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

    override fun getTotalTrainees(companyId: UUID, gender: Gender?, name: String?): Int {
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

    override fun getTrainer(trainerId: UUID, companyId: UUID): Trainer? =
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

    override fun isTraineeFromCompany(traineeId: UUID, companyId: UUID): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from company_trainee
                where trainee_id = :traineeId and company_id = :companyId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "companyId" to companyId
                )
            )
            .mapTo<Boolean>()
            .one()

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
}
