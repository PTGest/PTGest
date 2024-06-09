package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.traineeData.model.BodyData
import java.sql.ResultSet

class BodyDataMapper : ColumnMapper<BodyData> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): BodyData {
        val objectMapper = jacksonObjectMapper()
        val bodyDataString = r.getString("body_data")
        return objectMapper.readValue(bodyDataString)
    }
}
