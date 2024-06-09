package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import pt.isel.leic.ptgest.repository.TraineeDataRepo
import java.util.*

class JdbiTraineeDataRepo(private val handle: Handle) : TraineeDataRepo {
    override fun addTraineeData(traineeId: UUID, date: Date, bodyData: String): Int =
        handle.createUpdate(
            """
            insert into trainee_data (trainee_id, date, body_data)
            values (:traineeId, :date, :bodyData)
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date,
                    "bodyData" to bodyData
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getTraineeData(
        traineeId: UUID,
        skip: Int,
        limit: Int?,
        order: Order,
        date: Date?
    ): List<TraineeData> =
        handle.createQuery(
            """
            select id, trainee_id, date, body_data
            from trainee_data
            where trainee_id = :traineeId ${date?.let { "and date = :date" } ?: ""}
            order by date :order
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "skip" to skip,
                    "limit" to limit,
                    "order" to order,
                    "date" to date
                )
            )
            .mapTo<TraineeData>()
            .list()

    override fun getTotalTraineeData(traineeId: UUID, date: Date?): Int =
        handle.createQuery(
            """
            select count(*)
            from trainee_data
            where trainee_id = :traineeId ${date?.let { "and date = :date" } ?: ""}
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date
                )
            )
            .mapTo<Int>()
            .one()

    override fun getTraineeBodyDataDetails(traineeId: UUID, dataId: Int): TraineeDataDetails? =
        handle.createQuery(
            """
            select date, body_data
            from trainee_data
            where trainee_id = :traineeId and id = :dataId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "dataId" to dataId
                )
            )
            .mapTo<TraineeDataDetails>()
            .firstOrNull()
}
