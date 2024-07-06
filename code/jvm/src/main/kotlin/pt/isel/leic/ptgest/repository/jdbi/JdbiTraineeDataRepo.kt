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
            values (:traineeId, :date, :bodyData::jsonb)
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
        order: Order,
        skip: Int,
        limit: Int?
    ): List<TraineeData> {
        // Determine order direction as a string
        val orderDirection = if (order == Order.ASC) "ASC" else "DESC"

        // Build the base query
        val queryBuilder = StringBuilder(
            """
        select id, trainee_id, date, body_data
        from trainee_data
        where trainee_id = :traineeId
        order by date $orderDirection
            """.trimIndent()
        )

        // Append limit and offset
        if (limit != null) {
            queryBuilder.append(" limit :limit")
        }
        queryBuilder.append(" offset :skip")

        // Create the query
        val query = handle.createQuery(queryBuilder.toString())
            .bind("traineeId", traineeId)
            .bind("skip", skip)

        // Bind limit if it's not null
        if (limit != null) {
            query.bind("limit", limit)
        }

        return query
            .mapTo<TraineeData>()
            .list()
    }

    override fun getTotalTraineeData(traineeId: UUID): Int =
        handle.createQuery(
            """
            select count(*)
            from trainee_data
            where trainee_id = :traineeId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId
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
