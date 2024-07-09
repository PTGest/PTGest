package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback
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
            values (:traineeId, :workoutId, :beginDate, :endDate, :location, :type::session_type, :notes)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "workoutId" to workoutId,
                    "beginDate" to beginDate,
                    "endDate" to endDate,
                    "location" to location,
                    "type" to type.name,
                    "notes" to notes
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

    override fun getTrainerSessions(trainerId: UUID, date: Date): List<Int> =
        handle.createQuery(
            """
            select s.id
            from session s
            join session_trainer st on s.id = st.session_id
            left join cancelled_session cs on s.id = cs.session_id
            where trainer_id = :trainerId and DATE(s.begin_date) = DATE(:date) and cs.session_id is null
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "date" to date
                )
            )
            .mapTo<Int>()
            .list()

    override fun getTrainerSessions(trainerId: UUID, date: Date?, skip: Int, limit: Int?): List<TrainerSession> {
        val dateCondition = date?.let { "and DATE(s.begin_date) = DATE(:date)" } ?: ""

        return handle.createQuery(
            """
            select s.id, u.name as trainee_name, s.begin_date, s.end_date, s.type,
                case when cs.session_id is not null then true else false end as cancelled
            from session s
            join session_trainer st on s.id = st.session_id
            join "user" u on s.trainee_id = u.id
            left join cancelled_session cs on s.id = cs.session_id
            where trainer_id = :trainerId  $dateCondition
            order by s.begin_date
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "date" to date,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<TrainerSession>()
            .list()
    }

    override fun getTotalTrainerSessions(trainerId: UUID, date: Date?): Int {
        val dateCondition = date?.let { "and DATE(s.begin_date) = DATE(:date)" } ?: ""

        return handle.createQuery(
            """
        select count(*)
        from session s
        join session_trainer st on s.id = st.session_id
        where trainer_id = :trainerId $dateCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "date" to date
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getTraineeSessions(
        traineeId: UUID?,
        sessionType: SessionType?,
        date: Date?,
        skip: Int,
        limit: Int?
    ): List<Session> {
        val sessionTypeCondition = sessionType?.let { "and s.type = :type::session_type" } ?: ""
        val dateCondition = date?.let { "and DATE(s.begin_date) = DATE(:date)" } ?: ""

        return handle.createQuery(
            """
            select s.id, s.begin_date, s.end_date, s.type,
                case when cs.session_id is not null then true else false end as cancelled
            from session s
            left join cancelled_session cs on s.id = cs.session_id
            where s.trainee_id = :traineeId $sessionTypeCondition $dateCondition
            order by s.begin_date
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "type" to sessionType?.name,
                    "date" to date,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<Session>()
            .list()
    }

    override fun getTotalTraineeSessions(traineeId: UUID?, sessionType: SessionType?, date: Date?): Int {
        val sessionTypeCondition = sessionType?.let { "and type = :type::session_type" } ?: ""
        val dateCondition = date?.let { "and DATE(begin_date) = DATE(:date)" } ?: ""

        return handle.createQuery(
            """
            select count(*)
            from session
            where trainee_id = :traineeId $sessionTypeCondition $dateCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "type" to sessionType?.name,
                    "date" to date
                )
            )
            .mapTo<Int>()
            .one()
    }

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
            select s.id, u.name as trainee_name, s.workout_id, s.begin_date, s.end_date, s.location, 
                   s.type, s.notes,
                   case when cs.session_id is not null then true else false end as cancelled, cs.reason, cs.source
            from session s
            left join cancelled_session cs on s.id = cs.session_id
            join "user" u on s.trainee_id = u.id
            where s.id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<TrainerSessionDetails>()
            .firstOrNull()

    override fun getSessionDetails(sessionId: Int): SessionDetails? =
        handle.createQuery(
            """
            select id, workout_id, begin_date, end_date, location, type, notes,
                case when cs.session_id is not null then true else false end as cancelled,
                cs.reason, cs.source
            from session s
            left join cancelled_session cs on s.id = cs.session_id
            where s.id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<SessionDetails>()
            .firstOrNull()

    override fun getSessionDetails(traineeId: UUID, sessionId: Int): SessionDetails? =
        handle.createQuery(
            """
            select id, workout_id, begin_date, end_date, location, type, notes,
                case when cs.session_id is not null then true else false end as cancelled, 
                cs.reason, cs.source
            from session s
            left join cancelled_session cs on s.id = cs.session_id
            where s.id = :sessionId and trainee_id = :traineeId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "sessionId" to sessionId,
                    "traineeId" to traineeId
                )
            )
            .mapTo<SessionDetails>()
            .firstOrNull()

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
            set workout_id = :workoutId, 
                begin_date = :beginDate, 
                end_date = :endDate, 
                location = :location, 
                type = :type::session_type, 
                notes = :notes
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

    override fun cancelSession(sessionId: Int, source: Source, reason: String?) {
        handle.createUpdate(
            """
            insert into cancelled_session (session_id, source, reason)
            values (:sessionId, :source::source, :reason)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "sessionId" to sessionId,
                    "source" to source.name,
                    "reason" to reason
                )
            )
            .execute()
    }

    override fun createFeedback(source: Source, feedback: String, date: Date): Int =
        handle.createUpdate(
            """
            insert into feedback (source, feedback, date)
            values (:source::source, :feedback, :date)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "source" to source.name,
                    "feedback" to feedback,
                    "date" to date
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getSessionFeedbacks(sessionId: Int): List<SessionFeedback> =
        handle.createQuery(
            """
            select id, source, feedback, date
            from feedback
            join session_feedback sf on feedback.id = sf.feedback_id
            where sf.session_id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<SessionFeedback>()
            .list()

    override fun getSessionFeedback(sessionId: Int, feedbackId: Int): SessionFeedback? =
        handle.createQuery(
            """
            select id, source, feedback, date
            from feedback
            join session_feedback sf on feedback.id = sf.feedback_id
            where feedback.id = :feedbackId and sf.session_id = :sessionId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "feedbackId" to feedbackId,
                    "sessionId" to sessionId
                )
            )
            .mapTo<SessionFeedback>()
            .firstOrNull()

    override fun getSetSessionFeedbacks(sessionId: Int): List<SetSessionFeedback> =
        handle.createQuery(
            """
        select id, set_order_id, set_id as setId, source, feedback, date
        from feedback
        join session_set_feedback ssf on feedback.id = ssf.feedback_id
        where ssf.session_id = :sessionId
            """.trimIndent()
        )
            .bind("sessionId", sessionId)
            .mapTo<SetSessionFeedback>()
            .list()

    override fun getSetSessionFeedback(
        feedbackId: Int,
        sessionId: Int,
        setOrderId: Int,
        setId: Int
    ): SetSessionFeedback? =
        handle.createQuery(
            """
            select id, set_order_id, set_id, source, feedback, date
            from feedback f
            join session_set_feedback ssf on f.id = ssf.feedback_id
            where f.id = :feedbackId and ssf.session_id = :sessionId
            and ssf.set_order_id = :setOrderId and ssf.set_id = :setId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "feedbackId" to feedbackId,
                    "sessionId" to sessionId,
                    "setOrderId" to setOrderId,
                    "setId" to setId
                )
            )
            .mapTo<SetSessionFeedback>()
            .firstOrNull()

    override fun createSessionFeedback(feedbackId: Int, sessionId: Int) {
        handle.createUpdate(
            """
            insert into session_feedback (feedback_id, session_id)
            values (:feedbackId, :sessionId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "feedbackId" to feedbackId,
                    "sessionId" to sessionId
                )
            )
            .execute()
    }

    override fun validateSessionSet(sessionId: Int, setId: Int, setOrderId: Int): Boolean =
        handle.createQuery(
            """
            select exists (
                select 1
                from session s join workout_set ws on s.workout_id = ws.workout_id
                where s.id = :sessionId and ws.order_id = :setOrderId and ws.set_id = :setId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "sessionId" to sessionId,
                    "setOrderId" to setOrderId,
                    "setId" to setId
                )
            )
            .mapTo<Boolean>()
            .one()

    override fun createSessionSetFeedback(
        feedbackId: Int,
        sessionId: Int,
        workoutId: Int,
        setOrderId: Int,
        setId: Int
    ) {
        handle.createUpdate(
            """
            insert into session_set_feedback (feedback_id, session_id, workout_id, set_order_id, set_id)
            values (:feedbackId, :sessionId, :workoutId, :setOrderId, :setId)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "feedbackId" to feedbackId,
                    "sessionId" to sessionId,
                    "workoutId" to workoutId,
                    "setOrderId" to setOrderId,
                    "setId" to setId
                )
            )
            .execute()
    }

    override fun editFeedback(feedbackId: Int, feedback: String, date: Date) {
        handle.createUpdate(
            """
        update feedback
        set feedback = :feedback, date = :date
        where id = :feedbackId
        """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "feedbackId" to feedbackId,
                    "feedback" to feedback,
                    "date" to date
                )
            )
            .execute()
    }
}
