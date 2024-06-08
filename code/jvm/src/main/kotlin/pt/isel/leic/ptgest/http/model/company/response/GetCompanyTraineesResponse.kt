package pt.isel.leic.ptgest.http.model.company.response

import pt.isel.leic.ptgest.domain.trainee.model.Trainee

data class GetCompanyTraineesResponse(
    val trainees: List<Trainee>,
    val total: Int
)
