package pt.isel.leic.ptgest.http.controllers.workout.model.response

import pt.isel.leic.ptgest.domain.workout.model.Set

data class GetSetsResponse(
    val sets: List<Set>,
    val total: Int
)
