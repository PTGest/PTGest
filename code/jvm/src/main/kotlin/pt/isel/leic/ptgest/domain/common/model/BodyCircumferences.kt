package pt.isel.leic.ptgest.domain.common.model

data class BodyCircumferences(
    val neck: Double,
    val chest: Double,
    val waist: Double,
    val hips: Double,
    val thighs: Double,
    val calves: Double,
    val relaxedRightArm: Double,
    val relaxedLeftArm: Double,
    val flexedRightArm: Double,
    val flexedLeftArm: Double,
    val forearm: Double
) {
    init {
        require(neck > 0) { "Neck circumference must be a positive number" }
        require(chest > 0) { "Chest circumference must be a positive number" }
        require(waist > 0) { "Waist circumference must be a positive number" }
        require(hips > 0) { "Hips circumference must be a positive number" }
        require(thighs > 0) { "Thighs circumference must be a positive number" }
        require(calves > 0) { "Calves circumference must be a positive number" }
        require(relaxedRightArm > 0) { "Relaxed right arm circumference must be a positive number" }
        require(relaxedLeftArm > 0) { "Relaxed left arm circumference must be a positive number" }
        require(flexedRightArm > 0) { "Flexed right arm circumference must be a positive number" }
        require(flexedLeftArm > 0) { "Flexed left arm circumference must be a positive number" }
        require(forearm > 0) { "Forearm circumference must be a positive number" }
    }
}
