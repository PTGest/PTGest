package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.set.ExerciseDetailsType
import java.sql.ResultSet

class SetExerciseDetailsMapper : ColumnMapper<Map<ExerciseDetailsType, Number>> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): Map<ExerciseDetailsType, Number> {
        val json = r.getString(columnNumber)
        val typeRef = object : TypeReference<Map<ExerciseDetailsType, Number>>() {}
        return jacksonObjectMapper().readValue(json, typeRef)
    }
}
