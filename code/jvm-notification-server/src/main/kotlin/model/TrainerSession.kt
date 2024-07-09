package model

import java.util.*

data class TrainerSession(
    val traineeName: String,
    val beginDate: Date,
    val endDate: Date,
    val location: String,
)