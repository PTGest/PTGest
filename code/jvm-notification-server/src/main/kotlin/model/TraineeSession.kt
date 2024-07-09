package model

import java.util.*

data class TraineeSession(
    val trainerName: String?,
    val beginDate: Date,
    val endDate: Date?,
    val sessionType: SessionType,
    val location: String?,
    val notes: String?
)
