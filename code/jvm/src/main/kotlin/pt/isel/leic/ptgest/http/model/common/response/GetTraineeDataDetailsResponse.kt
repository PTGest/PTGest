package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.traineeData.model.BodyData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import java.util.*

data class GetTraineeDataDetailsResponse(
    val date: Date,
    val bodyData: BodyData
) {
    constructor(traineeDataDetails: TraineeDataDetails) : this(
        traineeDataDetails.date,
        traineeDataDetails.bodyData
    )
}
