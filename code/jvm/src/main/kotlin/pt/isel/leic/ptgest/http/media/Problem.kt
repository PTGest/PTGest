package pt.isel.leic.ptgest.http.media

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.net.URI

data class Problem(
    val type: URI,
    val title: String,
    val status: Int
) {
    fun toResponse() = ResponseEntity
        .status(status)
        .header("Content-Type", PROBLEM_MEDIA_TYPE)
        .body<Problem>(this)

    companion object {
        const val PROBLEMS_DOCS_URI = "https://github.com/PTGest/PTGest/tree/main/docs/problems/"

        private const val APPLICATION_TYPE = "application"
        private const val PROBLEM_SUBTYPE = "problem+json"
        const val PROBLEM_MEDIA_TYPE = "$APPLICATION_TYPE/$PROBLEM_SUBTYPE"

        @Suppress("unused")
        val problemMediaType = MediaType(APPLICATION_TYPE, PROBLEM_SUBTYPE)
    }
}
