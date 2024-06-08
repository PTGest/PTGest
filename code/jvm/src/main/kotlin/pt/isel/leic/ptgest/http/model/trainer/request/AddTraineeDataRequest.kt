package pt.isel.leic.ptgest.http.model.trainer.request

import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.traineeData.SkinFold
import pt.isel.leic.ptgest.domain.traineeData.model.BodyCircumferences
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
