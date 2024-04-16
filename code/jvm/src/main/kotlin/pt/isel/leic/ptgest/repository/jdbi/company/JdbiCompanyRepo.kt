package pt.isel.leic.ptgest.repository.jdbi.company

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.company.Trainer
import pt.isel.leic.ptgest.repository.CompanyRepo
import java.util.*

class JdbiCompanyRepo(private val handle: Handle) : CompanyRepo {

//  TODO: Add filter for gender
//  TODO: check if we need to add more filters
    override fun getCompanyTrainers(userId: UUID, skip: Int, limit: Int?): List<Trainer> =
        handle.createQuery(
            """
                select id, name, gender 
                from company_pt c_pt join (
                    select u.id as id, name, gender
                    from "user" u join dev.personal_trainer pt on u.id = pt.id
                ) ptd on c_pt.pt_id = ptd.id
                where company_id = :companyId
                limit :limit offset :skip
            """.trimIndent()
        )
            .bind("userId", userId)
            .bind("skip", skip)
            .bind("limit", limit)
            .mapTo<Trainer>()
            .list()

    override fun getTotalCompanyTrainers(userId: UUID): Int =
        handle.createQuery(
            """
                select count(*)
                from company_pt 
                where company_id = :companyId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .one()
}
