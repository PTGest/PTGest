package pt.isel.leic.ptgest.http.model.trainer.response

import pt.isel.leic.ptgest.domain.workout.model.Set

data class GetSetsResponse(
    val sets: List<Set>,
    val total: Int
)
