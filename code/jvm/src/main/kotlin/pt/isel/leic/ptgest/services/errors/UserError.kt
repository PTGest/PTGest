package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class UserError : BaseError() {

    data object UserNotFound : UserError() {
        private fun readResolve(): Any = UserNotFound
        override val message: String get() = "User not found."
    }
}
