package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class JsonbMapper : ColumnMapper<Map<String, Any>> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): Map<String, Any> {
        val json = r.getString(columnNumber)
        val typeRef = object : TypeReference<Map<String, Any>>() {}
        return jacksonObjectMapper().readValue(json, typeRef)
    }
}
