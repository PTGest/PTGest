package pt.isel.leic.ptgest.repository.jdbi.company

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.company.Trainer
import pt.isel.leic.ptgest.domain.workout.ExerciseDetails
import pt.isel.leic.ptgest.repository.CompanyRepo
import java.util.*

class JdbiCompanyRepo(private val handle: Handle) : CompanyRepo {

//  TODO: Add filter for gender and capacity
//  TODO: check if we need to add more filters
    override fun getCompanyTrainers(userId: UUID, skip: Int, limit: Int?): List<Trainer> =
        handle.createQuery(
            """
                select id, name, gender, capacity, total_trainees
                from company_trainer c_pt
                         join (select id, name, gender, count(*) as total_trainees
                               from (select u.id as id, name, gender
                                     from "user" u
                                              join trainer pt on u.id = pt.id) u_d
                                        join trainer_trainee t_t on u_d.id = t_t.trainer_id
                               group by name, gender) ptd on c_pt.trainer_id = ptd.id
                where company_id = :companyId
                limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "companyId" to userId,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<Trainer>()
            .list()

    override fun getTotalCompanyTrainers(userId: UUID): Int =
        handle.createQuery(
            """
                select count(*)
                from company_pt 
                where company_id = :companyId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .one()

    override fun getCompanyTrainer(trainerId: UUID, companyId: UUID): Trainer? =
        handle.createQuery(
            """
                select id, name, gender, capacity, total_trainees
                from company_trainer c_pt
                         join (select id, name, gender, count(*) as total_trainees
                               from (select u.id as id, name, gender
                                     from "user" u
                                              join trainer pt on u.id = pt.id) u_d
                                        join trainer_trainee t_t on u_d.id = t_t.trainer_id
                               group by name, gender) ptd on c_pt.trainer_id = ptd.id
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
            select id, name, description, muscle_group, type, ref
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
