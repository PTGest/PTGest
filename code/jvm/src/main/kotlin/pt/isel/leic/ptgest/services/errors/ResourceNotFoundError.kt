package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

data object ResourceNotFoundError : BaseError() {
    private fun readResolve(): Any = ResourceNotFoundError
    override val message: String get() = "Resource not found."
}
