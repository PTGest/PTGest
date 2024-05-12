package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.company.model.Trainee
import pt.isel.leic.ptgest.domain.company.model.Trainer
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
        name: String?
    ): List<Trainer> {
        val genderCondition = if (gender != null) "and t.gender = :gender" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select id, name, gender, total_trainees, capacity
            from company_trainer c_pt join (
                select u_d.id, u_d.name, u_d.gender, count(t_t.trainee_id) as total_trainees
                from (
                    select u.id as id, name, gender
                    from "user" u join trainer pt on u.id = pt.id
                ) u_d left join trainer_trainee t_t on u_d.id = t_t.trainer_id
                group by u_d.id, u_d.name, u_d.gender
            ) ptd on c_pt.trainer_id = ptd.id
            where company_id = :companyId $genderCondition $nameCondition
            order by (capacity - total_trainees) ${availability.name.lowercase()}
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

    override fun getTotalCompanyTrainers(companyId: UUID, gender: Gender?, name: String?): Int {
        val genderCondition = if (gender != null) "and t.gender = :gender" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from company_trainer c_pt join (
                select u_d.id, u_d.name, u_d.gender, count(t_t.trainee_id) as total_trainees
                from (
                    select u.id as id, name, gender
                    from "user" u join trainer pt on u.id = pt.id
                ) u_d left join trainer_trainee t_t on u_d.id = t_t.trainer_id
                group by u_d.id, u_d.name, u_d.gender
            ) ptd on c_pt.trainer_id = ptd.id
            where company_id = :companyId $genderCondition $nameCondition
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

    override fun getCompanyTrainees(
        companyId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee> {
        val genderCondition = if (gender != null) "and gender = :gender" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select u.id, name, gender
            from "user" u join (
                select id, gender
                from trainee t join (
                    select trainee_id
                    from company_trainee
                    where company_id = :companyId
                ) tId on t.id = tId.trainee_id
            ) trainee on u.id = trainee.id
            ${if (name != null || gender != null) "where" else ""} $genderCondition $nameCondition
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
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from "user" u join (
                select id, gender
                from trainee t join (
                    select trainee_id
                    from company_trainee
                    where company_id = :companyId
                ) tId on t.id = tId.trainee_id
            ) trainee on u.id = trainee.id
            ${if (name != null || gender != null) "where" else ""} $genderCondition $nameCondition
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
            select id, name, gender, capacity, total_trainees
            from company_trainer c_pt join (
                select id, name, gender, count(t_t.trainee_id) as total_trainees
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

    override fun getTrainerAssigned(traineeId: UUID): UUID =
        handle.createQuery(
            """
                select trainer_id
                from trainer_trainee
                where trainee_id = :traineeId
            """.trimIndent()
        )
            .bind("traineeId", traineeId)
            .mapTo<UUID>()
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

    override fun getExerciseDetails(companyId: UUID, exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select name, description, muscle_group, type, ref
            from exercise e join exercise_company et on e.id = et.company_id
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
