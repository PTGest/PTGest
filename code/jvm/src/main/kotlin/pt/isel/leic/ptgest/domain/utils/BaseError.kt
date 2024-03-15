package pt.isel.leic.ptgest.domain.utils

/**
 * Base error interface for all errors.
 * @property message The error message to be displayed.
 */
abstract class BaseError : Exception() {
    override val message: String = ""
}