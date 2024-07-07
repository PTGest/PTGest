package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

data object InaccessibleRecourse : BaseError() {
    private fun readResolve(): Any = InaccessibleRecourse
    override val message: String get() = "Inaccessible recourse."
}
