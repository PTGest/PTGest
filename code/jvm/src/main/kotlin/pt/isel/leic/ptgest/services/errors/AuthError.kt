package pt.isel.leic.ptgest.services.errors

import pt.isel.leic.ptgest.domain.utils.BaseError

sealed class AuthError : BaseError() {

    sealed class UserRegistrationError : AuthError() {
        data object UserAlreadyExists : UserRegistrationError() {
            private fun readResolve(): Any = UserAlreadyExists
            override val message: String get() = "User already exists."
        }
    }

    sealed class UserAuthenticationError : AuthError() {

        data object PasswordRequestExpired : UserAuthenticationError() {
            private fun readResolve(): Any = PasswordRequestExpired
            override val message: String get() = "Password request expired."
        }

        data object InvalidPasswordResetRequest : UserAuthenticationError() {
            private fun readResolve(): Any = InvalidPasswordResetRequest
            override val message: String get() = "Invalid password reset request."
        }

        data object InvalidTokenVersion : UserAuthenticationError() {
            private fun readResolve(): Any = InvalidTokenVersion
            override val message: String get() = "Invalid token version."
        }

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

        data object InvalidUserRoleException : UserAuthenticationError() {
            private fun readResolve(): Any = InvalidUserRoleException
            override val message: String
                get() =
                    "User role does not match the role in the token."
        }
    }
}
