package pt.isel.leic.ptgest.domain.traineeData.model

import pt.isel.leic.ptgest.domain.traineeData.SkinFold

data class BodyData(
    val weight: Double,
    val height: Double,
    val bodyCircumferences: BodyCircumferences,
    val bodyComposition: BodyComposition,
    val skinFolds: Map<SkinFold, Double>? = null
)
