package pt.isel.leic.ptgest.repository.jdbi.auth.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Role
import java.sql.ResultSet
import java.util.*

class TokenDetailsMapper : RowMapper<TokenDetails> {
    override fun map(rs: ResultSet, ctx: StatementContext?): TokenDetails =
        TokenDetails(
            tokenHash = rs.getString("token_hash"),
            userId = UUID.fromString(rs.getString("user_id")),
            role = Role.valueOf(rs.getString("role")),
            creationDate = rs.getDate("creation_date").toLocalDate(),
            expirationDate = rs.getDate("expiration_date").toLocalDate()
        )
}