package repositories

import model.SessionType
import model.TraineeSession
import model.TrainerSession
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.ResultSet
import java.util.Date
import java.util.UUID

class SessionRepo(private val jdbi: Jdbi) {
    fun getUserEmail(userId: UUID): String =
        jdbi.withHandle<String, Exception> { handle ->
            handle.createQuery("select email from \"user\" where id = :userId")
                .bind("userId", userId)
                .mapTo<String>()
                .one()
        }


    fun getTrainers(date: Date): List<UUID> =
        jdbi.withHandle<List<UUID>, Exception> { handle ->
            handle.createQuery("""
                        select trainer_id
                        from session_trainer st
                        left join session on st.session_id = session.id
                        where DATE(session.begin_date) = DATE(:date)
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
        }

    fun getTrainees(date: Date): List<UUID> =
        jdbi.withHandle<List<UUID>, Exception> { handle ->
            handle.createQuery("""
                        select trainee_id
                        from session
                        where DATE(session.begin_date) = DATE(:date)
                        and not exists (
                            select 1 from cancelled_session cs
                            where cs.session_id = session.id
                        )
                        group by trainee_id
                    """.trimIndent()
            )
                .bind("date", date)
                .mapTo<UUID>()
                .list()
        }

    fun getTrainerSessions(trainerId: UUID, date: Date) : List<TrainerSession> =
        jdbi.withHandle<List<TrainerSession>, Exception> { handle ->
            handle.createQuery("""
                select
                    trainee.name as trainee_name,
                    session.begin_date,
                    session.end_date
                    session.location
                from session
                join "user" trainee on session.trainee_id = trainee.id
                join session_trainer st on session.id = st.session_id
                where st.trainer_id = :trainerId
                and DATE(session.begin_date) = DATE(:date)
                and not exists (
                    select 1 from cancelled_session cs
                    where cs.session_id = session.id
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
                        parseDate(rs) ?: throw IllegalStateException("Date is null"),
                        parseDate(rs) ?: throw IllegalStateException("Date is null"),
                        rs.getString("location")
                    )
                }
                .list()
        }


    fun getTraineeSessions(traineeId: UUID, date: Date) : List<TraineeSession> =
        jdbi.withHandle<List<TraineeSession>, Exception> { handle ->
            handle.createQuery("""
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
                and DATE(session.begin_date) = DATE(:date)
                and not exists (
                    select 1 from cancelled_session cs
                    where cs.session_id = session.id
                )
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
                        parseDate(rs) ?: throw IllegalStateException("Date is null"),
                        parseDate(rs) ?: throw IllegalStateException("Date is null"),
                        SessionType.valueOf(rs.getString("type")),
                        rs.getString("location"),
                        rs.getString("notes")
                    )
                }
                .list()
        }


    private fun parseDate(rs: ResultSet): Date? {
        val timestamp = rs.getTimestamp("begin_date")
        return timestamp?.let { Date(it.time) }
    }
}