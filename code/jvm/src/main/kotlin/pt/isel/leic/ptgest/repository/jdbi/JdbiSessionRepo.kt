package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.TrainerSession
import pt.isel.leic.ptgest.domain.session.model.TrainerSessionDetails
import pt.isel.leic.ptgest.repository.SessionRepo
import java.util.*

class JdbiSessionRepo(private val handle: Handle) : SessionRepo {

    override fun createSession(
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    ): Int {
        return handle.createUpdate(
            """
            insert into session (trainee_id, workout_id, begin_date, end_date, location, type, notes)
            values (:traineeId, :workoutId, :beginDate, :endDate, :location, :type, :notes)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
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

    override fun getTrainerSessions(trainerId: UUID, skip: Int, limit: Int?): List<TrainerSession> =
        handle.createQuery(
            """
            select s.id, u.name as trainee_name, s.begin_date, s.end_date, s.type
            from session s
            join session_trainer st on s.id = st.session_id
            join "user" u on s.trainee_id = u.id
            where trainer_id = :trainerId
            order by s.begin_date
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<TrainerSession>()
            .list()

    override fun getTotalTrainerSessions(trainerId: UUID): Int =
        handle.createQuery(
            """
            select count(*)
            from session s
            join session_trainer st on s.id = st.session_id
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId
                )
            )
            .mapTo<Int>()
            .one()

    override fun getTraineeSessions(
        traineeId: UUID?,
        skip: Int,
        limit: Int?,
        sessionType: SessionType?
    ): List<Session> =
        handle.createQuery(
            """
            select id, begin_date, end_date, type
            from session
            where trainee_id = :traineeId ${sessionType?.let { "and type = :type" } ?: ""}
            order by begin_date
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "skip" to skip,
                    "limit" to limit,
                    "type" to sessionType?.name
                )
            )
            .mapTo<Session>()
            .list()

    override fun getTotalTraineeSessions(traineeId: UUID?, sessionType: SessionType?): Int =
        handle.createQuery(
            """
            select count(*)
            from session
            where trainee_id = :traineeId ${sessionType?.let { "and type = :type" } ?: ""}
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "type" to sessionType?.name
                )
            )
            .mapTo<Int>()
            .one()

    override fun getSessionTrainee(sessionId: Int): UUID {
        return handle.createQuery(
            """
            select trainee_id
            from session
            where id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<UUID>()
            .one()
    }

    override fun getTrainerSessionDetails(sessionId: Int): TrainerSessionDetails? =
        handle.createQuery(
            """
            select s.id, u.name as trainee_name, s.workout_id, s.begin_date, s.end_date, s.location, s.type, s.notes
            from session s
            join "user" u on s.trainee_id = u.id
            where s.id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<TrainerSessionDetails>()
            .firstOrNull()

    override fun getSessionDetails(sessionId: Int): Session? =
        handle.createQuery(
            """
            select id, begin_date, end_date, location, type, notes
            from session
            where id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<Session>()
            .one()

    override fun updateSession(
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    ) {
        handle.createUpdate(
            """
            update session
            set workout_id = :workoutId, begin_date = :beginDate, end_date = :endDate, location = :location, type = :type, notes = :notes
            where id = :sessionId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "sessionId" to sessionId,
                    "workoutId" to workoutId,
                    "beginDate" to beginDate,
                    "endDate" to endDate,
                    "location" to location,
                    "type" to type.name,
                    "notes" to notes
                )
            )
            .execute()
    }

    override fun cancelSession(sessionId: Int, reason: String?) {
        handle.createUpdate(
            """
            insert into cancelled_session (session_id, reason)
            values (:sessionId, :reason)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "sessionId" to sessionId,
                    "reason" to reason
                )
            )
            .execute()
    }
}