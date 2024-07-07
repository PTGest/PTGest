package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

data object BodyCompositionCalculationError : BaseError() {
    private fun readResolve(): Any = BodyCompositionCalculationError
    override val message: String get() = "Error calculating body composition values"
}
