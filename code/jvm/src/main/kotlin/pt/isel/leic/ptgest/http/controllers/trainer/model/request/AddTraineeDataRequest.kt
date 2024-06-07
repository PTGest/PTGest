package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.SkinFold
import pt.isel.leic.ptgest.domain.common.model.BodyCircumferences
import java.util.UUID

data class AddTraineeDataRequest(
    val traineeId: UUID,
    val gender: Gender,
    val weight: Double,
    val height: Double,
    val bodyCircumferences: BodyCircumferences,
    val bodyFatPercentage: Double?,
    val skinFold: Map<SkinFold, Double>?
)
