package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.common.Gender
import java.time.LocalDate
import java.util.*

interface AuthRepo {

    fun createUser(name: String, email: String, passwordHash: String): UUID

    fun createCompany(id: UUID)

    fun createIndependentTrainer(id: UUID, gender: Gender, phoneNumber: String?)

    fun getToken(tokenHash: String): TokenDetails?

    fun revokeToken(tokenHash: String)

    fun updateExpirationDate(tokenHash: String, expirationDate: LocalDate)
}