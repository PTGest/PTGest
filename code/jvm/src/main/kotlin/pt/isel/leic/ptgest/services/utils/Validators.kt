package pt.isel.leic.ptgest.services.utils

class Validators {
    data class ValidationRequest(val value: Any?, val message: String, val predicate: (Any?) -> Boolean)

    companion object {
        fun validate(vararg requests: ValidationRequest) {
            requests.forEach { request ->
                request.value?.let { require(request.predicate(request.value)) { request.message } }
            }
        }

        fun isYoutubeUrl(url: String): Boolean {
            val pattern = "^(https://)?((w){3}.)?youtube\\.com/watch\\?v=\\w+"
            val compiledPattern = Regex(pattern)
            return compiledPattern.matches(url)
        }
    }
}
