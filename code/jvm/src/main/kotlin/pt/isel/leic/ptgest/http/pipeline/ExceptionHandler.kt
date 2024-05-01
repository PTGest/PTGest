package pt.isel.leic.ptgest.http.pipeline

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException
import pt.isel.leic.ptgest.http.media.Problem
import pt.isel.leic.ptgest.http.media.Problem.Companion.PROBLEMS_DOCS_URI
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.company.CompanyError
import pt.isel.leic.ptgest.services.trainer.TrainerError
import pt.isel.leic.ptgest.services.user.UserError
import java.net.URI

@ControllerAdvice
class ExceptionHandler {

    companion object {
        fun Exception.toProblemType(): String =
            (this::class.simpleName ?: "Unknown")
                .replace("Exception", "")
                .replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}-${it.groupValues[2]}" }
                .lowercase()
    }

    @ExceptionHandler(
        value = [
            IllegalArgumentException::class,
            IllegalStateException::class,
            CompanyError.TrainerCapacityReached::class
        ]
    )
    fun handleBadRequest(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Bad Request",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            HttpRequestMethodNotSupportedException::class
        ]
    )
    fun handleMethodNotAllowed(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Method Not Allowed",
            status = HttpStatus.METHOD_NOT_ALLOWED.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            TypeMismatchException::class
        ]
    )
    fun handleTypeMismatch(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = "Invalid parameter value for query parameter. Check the documentation for the correct values.",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            AuthError.TokenError.TokenExpired::class,
            AuthError.TokenError.TokenExpirationMismatchException::class,
            AuthError.TokenError.InvalidRefreshToken::class,
            AuthError.TokenError.InvalidPasswordResetToken::class,
            AuthError.TokenError.UserIdMismatch::class,
            AuthError.UserAuthenticationError.InvalidPassword::class,
            AuthError.UserAuthenticationError.TokenNotProvided::class,
            io.jsonwebtoken.SignatureException::class,
            io.jsonwebtoken.ExpiredJwtException::class,
            AuthenticationException::class
        ]
    )
    fun handleUnauthorized(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Unauthorized",
            status = HttpStatus.UNAUTHORIZED.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            AuthError.UserAuthenticationError.UnauthorizedRole::class,
            AuthError.UserAuthenticationError.InvalidUserRoleException::class
        ]
    )
    fun handleForbidden(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Forbidden",
            status = HttpStatus.FORBIDDEN.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            NoHandlerFoundException::class,
            NotImplementedError::class,
            AuthError.UserAuthenticationError.UserNotFound::class,
            UserError.UserNotFound::class,
            CompanyError.TrainerNotFound::class,
            TrainerError.ExerciseNotFoundError::class
        ]
    )
    fun handleNotFound(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Not Found",
            status = HttpStatus.NOT_FOUND.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            AuthError.UserRegistrationError.UserAlreadyExists::class
        ]
    )
    fun handleConflict(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Conflict",
            status = HttpStatus.CONFLICT.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            HttpMessageNotReadableException::class
        ]
    )
    fun handleHttpMessageNotReadableExceptions(
        request: HttpServletRequest,
        ex: HttpMessageNotReadableException
    ): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + "invalid-request-body"),
            title = "Invalid request body${
            ex.rootCause.let {
                ": " + when (it) {
                    is UnrecognizedPropertyException -> "Unknown property '${it.propertyName}'"
                    is JsonParseException -> it.originalMessage
                    is MismatchedInputException -> "Missing property '${it.message}'"
                    else -> null
                }
            }
            }",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { ex.printStackTrace() }

    @ExceptionHandler(
        value = [
            MethodArgumentNotValidException::class
        ]
    )
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation Error",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { e.printStackTrace() }

    @ExceptionHandler(
        value = [
            Exception::class,
            TrainerError.InvalidSetTypeError::class
        ]
    )
    fun handleInternalServerError(e: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + e.toProblemType()),
            title = e.message ?: "Internal Server Error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        ).toResponse().also { e.printStackTrace() }
}
