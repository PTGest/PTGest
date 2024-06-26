package pt.isel.leic.ptgest.services.auth

import pt.isel.leic.ptgest.domain.utils.BaseError

/**
 * Base error interface for errors related to the validation of parameters related to authentication.
 */
sealed class AuthError : BaseError() {

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
     * Base class for token errors.
     */
    sealed class TokenError : AuthError() {

        data object TokenExpired : TokenError() {
            private fun readResolve(): Any = TokenExpired
            override val message: String get() = "Token has expired."
        }

        data object TokenExpirationMismatchException : TokenError() {
            private fun readResolve(): Any = TokenExpirationMismatchException
            override val message: String
                get() =
                    "The expiration date of the JWT token does not match the cookie’s maxAge."
        }

        data object InvalidRefreshToken : TokenError() {
            private fun readResolve(): Any = InvalidRefreshToken
            override val message: String get() = "Invalid refresh token."
        }

        data object InvalidPasswordResetToken : TokenError() {
            private fun readResolve(): Any = InvalidRefreshToken
            override val message: String get() = "Invalid password reset token."
        }

        data object UserIdMismatch : TokenError() {
            private fun readResolve(): Any = UserIdMismatch
            override val message: String get() = "User ID in refresh token does not match the ID in the access token."
        }

        data object InvalidTokenVersion : TokenError() {
            private fun readResolve(): Any = InvalidTokenVersion
            override val message: String get() = "Invalid token version."
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

        data object UnauthorizedRole : UserAuthenticationError() {
            private fun readResolve(): Any = UnauthorizedRole
            override val message: String get() = "User does not have the required role."
        }

        data object InvalidUserRoleException : TokenError() {
            private fun readResolve(): Any = InvalidUserRoleException
            override val message: String
                get() =
                    "User role does not match the role in the token."
        }
    }
}
