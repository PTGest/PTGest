package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.Date

class DateMapper : ColumnMapper<Date> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): Date {
        return Date(r.getTimestamp(columnNumber).time)
    }
}
