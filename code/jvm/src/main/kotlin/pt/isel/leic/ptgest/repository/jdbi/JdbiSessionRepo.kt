package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.repository.SessionRepo
import java.util.*

class JdbiSessionRepo(private val handle: Handle) : SessionRepo {

    override fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int =
        handle.createUpdate(
            """
            insert into session (trainee_id, trainer_id, workout_id, begin_date, end_date, type, notes)
            values (:traineeId, :trainerId, :workoutId, :beginDate, :endDate, :type, :notes)
            returning id
            """
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "trainerId" to trainerId,
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
