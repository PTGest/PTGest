package pt.isel.leic.ptgest.domain.traineeData.model

import pt.isel.leic.ptgest.services.errors.BodyCompositionCalculationError

data class BodyComposition(
    val bmi: Double,
    val bodyFatPercentage: Double? = null,
    val bodyFatMass: Double? = null,
    val fatFreeMass: Double? = null
) {
    init {
        if (bmi < 0) throw BodyCompositionCalculationError
        if (bodyFatPercentage != null && (bodyFatPercentage < 0 || bodyFatPercentage > 100)) throw BodyCompositionCalculationError
        if (bodyFatMass != null && bodyFatMass < 0) throw BodyCompositionCalculationError
        if (fatFreeMass != null && fatFreeMass < 0) throw BodyCompositionCalculationError
    }

    constructor(weight: Double, height: Double, bodyFatPercentage: Double) : this(
        bmi = calculateBMI(weight, height),
        bodyFatPercentage = bodyFatPercentage,
        bodyFatMass = calculateBodyFatMass(weight, bodyFatPercentage),
        fatFreeMass = calculateFatFreeMass(weight, calculateBodyFatMass(weight, bodyFatPercentage))
    )

    constructor(weight: Double, height: Double) : this(
        bmi = calculateBMI(weight, height)
    )

    companion object {
        private fun calculateBMI(weight: Double, height: Double): Double = weight / (height * height)
        private fun calculateBodyFatMass(weight: Double, bodyFatPercentage: Double): Double =
            weight * (bodyFatPercentage / 100)

        private fun calculateFatFreeMass(weight: Double, bodyFatMass: Double): Double = weight - bodyFatMass
    }
}
