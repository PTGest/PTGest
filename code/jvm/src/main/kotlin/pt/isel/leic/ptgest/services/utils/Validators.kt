package pt.isel.leic.ptgest.services.utils

class Validators {
    data class ValidationRequest(val value: Any?, val message: String, val predicate: (Any?) -> Boolean)

    companion object {
        fun validate(vararg requests: ValidationRequest) {
            requests.forEach {
                if (it.value != null) require(it.predicate(it.value)) { it.message }
            }
        }
    }
}
