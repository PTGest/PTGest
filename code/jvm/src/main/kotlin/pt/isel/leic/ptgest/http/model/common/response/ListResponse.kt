package pt.isel.leic.ptgest.http.model.common.response

data class ListResponse<T>(
    val items: List<T>,
    val total: Int
)
