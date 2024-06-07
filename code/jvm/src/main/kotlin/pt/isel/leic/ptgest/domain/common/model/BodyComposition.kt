package pt.isel.leic.ptgest.domain.common.model

data class BodyComposition(
    val bmi: Double,
    val bodyFatPercentage: Double? = null,
    val bodyFatMass: Double? = null,
    val fatFreeMass: Double? = null
) {

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
        private fun calculateBodyFatMass(weight: Double, bodyFatPercentage: Double): Double = weight * (bodyFatPercentage / 100)
        private fun calculateFatFreeMass(weight: Double, bodyFatMass: Double): Double = weight - bodyFatMass
    }
}
