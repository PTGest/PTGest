package pt.isel.leic.ptgest.http.controllers.company.model.response

import pt.isel.leic.ptgest.domain.company.model.Trainer

data class GetCompanyTrainersResponse(
    val trainers: List<Trainer>,
    val total: Int
)
