package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError


sealed class ValidationErrors : BaseError() {
    sealed class InvalidParameter : ValidationErrors() {
        object InvalidString : InvalidParameter() {
            private fun readResolve(): Any = InvalidString
            override val message: String get() = "An invalid string was provided. It must not be blank."
        }

        object InvalidInt : InvalidParameter() {
            private fun readResolve(): Any = InvalidInt
            override val message: String get() = "An invalid integer was provided. It must be greater than zero."
        }

        object InvalidUUID : InvalidParameter() {
            private fun readResolve(): Any = InvalidUUID
            override val message: String get() = "An invalid UUID was provided."
        }
    }
}
