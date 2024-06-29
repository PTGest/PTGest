package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.*

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {

    override fun getTrainees(trainerId: UUID, skip: Int, limit: Int?, gender: Gender?, name: String?): List<Trainee> {
        val genderCondition = if (gender != null) "and gender = :gender" else ""
        val nameCondition = if (name != null) "and ut.name like :name" else ""

        return handle.createQuery(
            """
            select t.id as trainee_id, ut.name as trainee_name, t.gender, upt.id as trainer_id, upt.name as trainer_name
            from trainee t
            join "user" ut on t.id = ut.id
            left join trainer_trainee tt on t.id = tt.trainee_id
            left join "user" upt on tt.trainer_id = upt.id
            where tt.trainer_id = :trainerId $genderCondition $nameCondition
            limit :limit offset :skip;
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "gender" to gender,
                    "name" to "%$name%"
                )
            )
            .mapTo<Trainee>()
            .list()
    }

    override fun getTotalTrainees(trainerId: UUID, gender: Gender?, name: String?): Int {
        val genderCondition = if (gender != null) "and gender = :gender" else ""
        val nameCondition = if (name != null) "and ut.name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from trainee t
            join "user" ut on t.id = ut.id
            left join trainer_trainee tt on t.id = tt.trainee_id
            left join "user" upt on tt.trainer_id = upt.id
            where tt.trainer_id = :trainerId $genderCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "gender" to gender,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getTrainerDetails(trainerId: UUID): TrainerDetails? =
        handle.createQuery(
            """
                select gender, phone_number
                from trainer 
                where trainer.id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<TrainerDetails>()
            .firstOrNull()

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

    override fun associateTrainerToReport(trainerId: UUID, reportId: Int) {
        handle.createUpdate(
            """
            insert into report_trainer (reportid, trainer_id)
            values (:reportId, :trainerId)
            """
        )
            .bindMap(
                mapOf(
                    "reportId" to reportId,
                    "trainerId" to trainerId
                )
            )
            .execute()
    }

    override fun getTraineeIdByName(name: String): UUID? {
        return handle.createQuery(
            """
            select id
            from "user" join trainee on id = trainee_id
            where name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<UUID>()
            .firstOrNull()
    }

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

    override fun associateTrainerToSet(trainerId: UUID, setId: Int) {
        handle.createUpdate(
            """
                insert into set_trainer (trainer_id, set_id)
                values (:trainerId, :setId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "setId" to setId
                )
            )
            .execute()
    }

    override fun associateTrainerToWorkout(trainerId: UUID, workoutId: Int) {
        handle.createUpdate(
            """
                insert into workout_trainer (trainer_id, workout_id)
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

    override fun associateSessionToTrainer(trainerId: UUID, sessionId: Int) {
        handle.createUpdate(
            """
            insert into session_trainer (trainer_id, session_id)
            values (:trainerId, :sessionId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "sessionId" to sessionId
                )
            )
            .execute()
    }
}
