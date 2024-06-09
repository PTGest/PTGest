package pt.isel.leic.ptgest.services.utils

class Validators {
    data class ValidationRequest(val value: Any?, val message: String, val predicate: (Any?) -> Boolean)

    companion object {

        //      TODO: check duration fields
        private val detailsValidators = mapOf(
            "reps" to { it: String ->
                val intValue = requireNotNull(it.toIntOrNull()) { "Reps must be an integer" }
                require(intValue > 0) { "Reps must be a positive integer" }
                intValue
            },
            "weight" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Weight must be a double" }
                require(doubleValue >= 0) { "Weight must be a positive double" }
                doubleValue
            },
            "distance" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Distance must be a double" }
                require(doubleValue > 0) { "Distance must be a positive double" }
                doubleValue
            },
            "speed" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Speed must be a double" }
                require(doubleValue > 0) { "Speed must be a positive double" }
                doubleValue
            },
            "time" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Time must be a double" }
                require(doubleValue > 0) { "Time must be a positive double" }
                doubleValue
            },
            "resistance" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Resistance must be a double" }
                require(doubleValue >= 0) { "Resistance must be a positive double" }
                doubleValue
            },
            "restTime" to { it: String ->
                val doubleValue = requireNotNull(it.toDoubleOrNull()) { "Rest time must be a double" }
                require(doubleValue >= 0) { "Rest time must be a positive double" }
                doubleValue
            }
        )

        fun validate(vararg requests: ValidationRequest) {
            requests.forEach { request ->
                request.value?.let { require(request.predicate(request.value)) { request.message } }
            }
        }

        fun validateSetDetails(details: Map<String, String>): Map<String, Any> =
            details.mapNotNull { (key, value) ->
                val validator = detailsValidators[key]
                if (validator != null) {
                    val validatedValue = validator.invoke(value)
                    key to validatedValue
                } else {
                    null
                }
            }.toMap()

        fun isYoutubeUrl(url: String): Boolean {
            val pattern = "^(https://)?((w){3}.)?youtube\\.com/watch\\?v=\\w+"
            val compiledPattern = Regex(pattern)
            return compiledPattern.matches(url)
        }
    }
}
