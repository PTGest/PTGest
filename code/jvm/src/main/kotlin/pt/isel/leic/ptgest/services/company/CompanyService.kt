package pt.isel.leic.ptgest.services.company

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.company.CompanyTrainers
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

@Service
class CompanyService(
    private val transactionManager: TransactionManager
) {

    fun getCompanyTrainers(
        skip: Int?,
        limit: Int?,
        userId: UUID
    ): CompanyTrainers =
        transactionManager.run {
            val companyRepo = it.companyRepo

            val totalResults = companyRepo.getTotalCompanyTrainers(userId)
            val trainers = companyRepo.getCompanyTrainers(userId, skip ?: 0, limit)

            return@run CompanyTrainers(trainers, totalResults)
        }
}
