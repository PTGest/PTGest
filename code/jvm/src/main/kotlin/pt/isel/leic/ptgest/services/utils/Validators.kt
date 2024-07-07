package pt.isel.leic.ptgest.services.utils

import java.util.*

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

        fun isCurrentDate24BeforeDate(date: Date): Boolean {
            val calendar = Calendar.getInstance()

            val currentDate = calendar.time

            calendar.time = date
            calendar.add(Calendar.HOUR, -24)
            val date24HoursBefore = calendar.time

            return currentDate.before(date24HoursBefore)
        }
    }
}
