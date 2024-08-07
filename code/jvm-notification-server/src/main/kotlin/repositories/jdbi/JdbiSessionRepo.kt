package repositories.jdbi

import model.TraineeSession
import model.TrainerSession
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repositories.SessionRepo
import java.sql.ResultSet
import java.util.*

class JdbiSessionRepo(private val handle: Handle) : SessionRepo {
    override fun getUserEmail(userId: UUID): String =

        handle.createQuery("select email from \"user\" where id = :userId")
            .bind("userId", userId)
            .mapTo<String>()
            .one()


    override fun getTrainers(date: Date): List<UUID> =

        handle.createQuery(
            """
                        select trainer_id
                        from session_trainer st
                        left join session s on st.session_id = s.id
                        where DATE(s.begin_date) = DATE(:date)
                        and not exists (
                            select 1 from cancelled_session cs
                            where cs.session_id = st.session_id
                        )
                        group by trainer_id
                    """.trimIndent()
        )
            .bind("date", date)
            .mapTo<UUID>()
            .list()


    override fun getTrainees(date: Date): List<UUID> =

        handle.createQuery(
            """
                        select trainee_id
                        from session s
                        where DATE(begin_date) = DATE(:date)
                        and not exists (
                            select 1 from cancelled_session cs
                            where cs.session_id = s.id
                        ) and type = 'TRAINER_GUIDED'
                        group by trainee_id
                    """.trimIndent()
        )
            .bind("date", date)
            .mapTo<UUID>()
            .list()

    override fun getTrainerSessions(trainerId: UUID, date: Date): List<TrainerSession> =

        handle.createQuery(
            """
                select 
                    trainee.name as trainee_name,
                    s.begin_date,
                    s.end_date,
                    s.location
                from session_trainer st
                left join session s on st.session_id = s.id
                join "user" trainee on s.trainee_id = trainee.id
                where st.trainer_id = :trainerId
                and DATE(s.begin_date) = DATE(:date)
                and not exists (
                    select 1 from cancelled_session cs
                    where cs.session_id = s.id
                )
                """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "date" to date
                )
            )
            .map { rs, _ ->
                TrainerSession(
                    rs.getString("trainee_name"),
                    rs.parseDate("begin_date"),
                    rs.parseDate("end_date"),
                    rs.getString("location")
                )
            }
            .list()

    override fun getTraineeSessions(traineeId: UUID, date: Date): List<TraineeSession> =
        handle.createQuery(
            """
                select
                    t.name as trainer_name,
                    s.begin_date,
                    s.end_date,
                    s.type,
                    s.location,
                    s.notes
                from session s
                left join session_trainer st on s.id = st.session_id
                left join "user" t on st.trainer_id = t.id
                where s.trainee_id = :traineeId
                and DATE(s.begin_date) = DATE(:date)
                and not exists (
                    select 1 from cancelled_session cs
                    where cs.session_id = s.id
                )
                and s.type = 'TRAINER_GUIDED'
                """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date
                )
            )
            .map { rs, _ ->
                TraineeSession(
                    rs.getString("trainer_name"),
                    rs.parseDate("begin_date"),
                    rs.parseDate("end_date"),
                    rs.getString("location"),
                    rs.getString("notes")
                )
            }
            .list()


    private fun ResultSet.parseDate(columnLabel: String): Date {
        val timestamp = this.getTimestamp(columnLabel)
        return timestamp?.let { Date(it.time) } ?: throw IllegalStateException("Date is null")
    }
}