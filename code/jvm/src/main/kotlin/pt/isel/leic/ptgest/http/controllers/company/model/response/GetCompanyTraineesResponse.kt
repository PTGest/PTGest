package pt.isel.leic.ptgest.http.controllers.company.model.response

import pt.isel.leic.ptgest.domain.company.model.CompanyTrainees
import pt.isel.leic.ptgest.domain.company.model.Trainee

data class GetCompanyTraineesResponse(
    val trainees: List<Trainee>,
    val total: Int
) {
    constructor(trainees: CompanyTrainees) : this(trainees.trainees, trainees.total)
}
