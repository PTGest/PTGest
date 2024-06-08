package pt.isel.leic.ptgest.http.model.company.response

import pt.isel.leic.ptgest.domain.trainer.model.Trainer

data class GetCompanyTrainersResponse(
    val trainers: List<Trainer>,
    val total: Int
)
