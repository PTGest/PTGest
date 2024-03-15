package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError


/**
 * Base error interface for errors related to the validation of parameters related to authentication.
 */
sealed class AuthError : BaseError() {

    sealed class TokenError : AuthError() {
        object TokenNotFound : TokenError() {
            private fun readResolve(): Any = TokenNotFound
            override val message: String get() = "Token not found."
        }

        object TokenInactive : TokenError() {
            private fun readResolve(): Any = TokenInactive
            override val message: String get() = "Token is not active. It may have been expired or revoked."
        }

        object TokenOwnershipError : TokenError() {
            private fun readResolve(): Any = TokenOwnershipError
            override val message: String get() = "Token not owned by user."
        }
    }

    /**
     * Base class for user registration errors.
     */
    sealed class UserRegistrationError : AuthError() {
        object UserAlreadyExists : UserRegistrationError() {
            private fun readResolve(): Any = UserAlreadyExists
            override val message: String get() = "User already exists."
        }
    }

    /**
     * Base class for user authentication errors.
     */
    sealed class UserAuthenticationError : AuthError() {

        object UserNotFound : UserAuthenticationError() {
            private fun readResolve(): Any = UserNotFound
            override val message: String get() = "User not found."
        }

        object InvalidPassword : UserAuthenticationError() {
            private fun readResolve(): Any = InvalidPassword
            override val message: String get() = "Invalid password for user."
        }

        object TokenNotProvided : UserAuthenticationError() {
            private fun readResolve(): Any = TokenNotProvided
            override val message: String get() = "Token not provided."
        }
    }
}