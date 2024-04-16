package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.company.Trainer
import java.util.*

interface CompanyRepo {

    fun getCompanyTrainers(userId: UUID, skip: Int, limit: Int?): List<Trainer>

    fun getTotalCompanyTrainers(userId: UUID): Int
}
