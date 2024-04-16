package pt.isel.leic.ptgest.http.controllers.company.model.response

data class GetCompanyTrainersResponse(
    val trainers: List<TrainerResponse>,
    val total: Int
)
