package pt.isel.leic.ptgest.services.auth

import pt.isel.leic.ptgest.domain.utils.BaseError


/**
 * Base error interface for errors related to the validation of parameters related to authentication.
 */
sealed class AuthError : BaseError() {

    sealed class TokenError : AuthError() {
        data object TokenNotFound : TokenError() {
            private fun readResolve(): Any = TokenNotFound
            override val message: String get() = "Token not found."
        }

        data object TokenExpired : TokenError() {
            private fun readResolve(): Any = TokenExpired
            override val message: String get() = "Token has expired."
        }

        data object TokenOwnershipError : TokenError() {
            private fun readResolve(): Any = TokenOwnershipError
            override val message: String get() = "Token not owned by user."
        }
    }

    /**
     * Base class for user registration errors.
     */
    sealed class UserRegistrationError : AuthError() {
        data object UserAlreadyExists : UserRegistrationError() {
            private fun readResolve(): Any = UserAlreadyExists
            override val message: String get() = "User already exists."
        }
    }

    /**
     * Base class for user authentication errors.
     */
    sealed class UserAuthenticationError : AuthError() {

        data object UserNotFound : UserAuthenticationError() {
            private fun readResolve(): Any = UserNotFound
            override val message: String get() = "User not found."
        }

        data object InvalidPassword : UserAuthenticationError() {
            private fun readResolve(): Any = InvalidPassword
            override val message: String get() = "Invalid password for user."
        }

        data object TokenNotProvided : UserAuthenticationError() {
            private fun readResolve(): Any = TokenNotProvided
            override val message: String get() = "Token not provided."
        }
    }
}