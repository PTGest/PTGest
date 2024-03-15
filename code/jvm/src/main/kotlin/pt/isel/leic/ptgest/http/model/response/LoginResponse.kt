package pt.isel.leic.ptgest.http.model.response

internal data class LoginResponse(
    val userId: Int,
    val token: String
)
