package pt.isel.leic.ptgest.http.pipeline

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.slf4j.LoggerFactory
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
import pt.isel.leic.ptgest.services.errors.AuthError
import pt.isel.leic.ptgest.services.errors.BodyCompositionCalculationError
import pt.isel.leic.ptgest.services.errors.CompanyError
import pt.isel.leic.ptgest.services.errors.InaccessibleRecourse
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import pt.isel.leic.ptgest.services.errors.TrainerError
import pt.isel.leic.ptgest.services.errors.UserError
import java.net.URI

@ControllerAdvice
class ExceptionAdvice {

    private val logger = LoggerFactory.getLogger(this::class.java)

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
            IllegalStateException::class
        ]
    )
    fun handleBadRequest(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Bad Request",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            HttpRequestMethodNotSupportedException::class
        ]
    )
    fun handleMethodNotAllowed(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Method Not Allowed",
            status = HttpStatus.METHOD_NOT_ALLOWED.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            TypeMismatchException::class
        ]
    )
    fun handleTypeMismatch(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = "Invalid parameter value for query parameter. Check the documentation for the correct values.",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            io.jsonwebtoken.SignatureException::class,
            io.jsonwebtoken.ExpiredJwtException::class,
            AuthenticationException::class,
            AuthError.UserAuthenticationError.PasswordRequestExpired::class,
            AuthError.UserAuthenticationError.InvalidTokenVersion::class,
            AuthError.UserAuthenticationError.InvalidPassword::class,
            AuthError.UserAuthenticationError.TokenNotProvided::class
        ]
    )
    fun handleUnauthorized(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Unauthorized",
            status = HttpStatus.UNAUTHORIZED.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            AuthError.UserAuthenticationError.UnauthorizedRole::class,
            AuthError.UserAuthenticationError.InvalidUserRoleException::class,
            InaccessibleRecourse::class
        ]
    )
    fun handleForbidden(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Forbidden",
            status = HttpStatus.FORBIDDEN.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            NoHandlerFoundException::class,
            NotImplementedError::class,
            AuthError.UserAuthenticationError.UserNotFound::class,
            CompanyError.TrainerNotFound::class,
            CompanyError.TraineeNotFromCompany::class,
            ResourceNotFoundError::class,
            TrainerError.TrainerNotAssignedToTraineeError::class,
            UserError.UserNotFound::class
        ]
    )
    fun handleNotFound(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Not Found",
            status = HttpStatus.NOT_FOUND.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            AuthError.UserRegistrationError.UserAlreadyExists::class,
            CompanyError.TrainerAlreadyAssociatedToTrainee::class,
            TrainerError.ResourceAlreadyFavoriteError::class,
            TrainerError.ResourceNotFavoriteError::class
        ]
    )
    fun handleConflict(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Conflict",
            status = HttpStatus.CONFLICT.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            HttpMessageNotReadableException::class
        ]
    )
    fun handleHttpMessageNotReadableExceptions(ex: HttpMessageNotReadableException): ResponseEntity<Problem> =
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
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            MethodArgumentNotValidException::class
        ]
    )
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation Error",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            Exception::class,
            RuntimeException::class,
            BodyCompositionCalculationError::class
        ]
    )
    fun handleInternalServerError(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Internal Server Error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        ).toResponse().also { logger.error(ex.message, ex) }

    @ExceptionHandler(
        value = [
            CompanyError.TrainerCapacityReached::class
        ]
    )
    fun handleUnprocessableEntity(ex: Exception): ResponseEntity<Problem> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Unprocessable Entity",
            status = HttpStatus.UNPROCESSABLE_ENTITY.value()
        ).toResponse().also { logger.error(ex.message, ex) }
}
