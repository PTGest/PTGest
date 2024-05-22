package pt.isel.leic.ptgest.services.utils

class Validators {
    data class ValidationRequest(val value: Any?, val message: String, val predicate: (Any?) -> Boolean)

    companion object {
        fun validate(vararg requests: ValidationRequest) {
            requests.forEach { request ->
                request.value?.let { require(request.predicate(request.value)) { request.message } }
            }
        }
    }
}
