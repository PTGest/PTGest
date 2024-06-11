package pt.isel.leic.ptgest.http.model.trainer.response

import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.TrainerSessionDetails
import java.util.*

data class GetSessionDetails(
    val id: Int,
    val traineeName: String,
    val workoutId: Int,
    val beginDate: Date,
    val endDate: Date?,
    val location: String?,
    val type: SessionType,
    val notes: String?
) {
    constructor(trainerSessionDetails: TrainerSessionDetails) : this(
        trainerSessionDetails.id,
        trainerSessionDetails.traineeName,
        trainerSessionDetails.workoutId,
        trainerSessionDetails.beginDate,
        trainerSessionDetails.endDate,
        trainerSessionDetails.location,
        trainerSessionDetails.type,
        trainerSessionDetails.notes
    )
}
