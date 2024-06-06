package pt.isel.leic.ptgest.domain.trainer.model

data class Report(
    val id: Int,
    val trainee: String,
    val date: String,
    val visibility: Boolean
)
