package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import java.util.*

interface TraineeDataRepo {

    fun addTraineeData(traineeId: UUID, date: Date, bodyData: String): Int

    fun getTraineeData(
        traineeId: UUID,
        order: Order,
        skip: Int,
        limit: Int?
    ): List<TraineeData>

    fun getTotalTraineeData(traineeId: UUID): Int

    fun getTraineeBodyDataDetails(traineeId: UUID, dataId: Int): TraineeDataDetails?
}
