package pt.isel.leic.ptgest.http.controllers.company.model.response

data class GetCompanyTraineesResponse(
    val trainees: List<TraineeResponse>,
    val total: Int
)
