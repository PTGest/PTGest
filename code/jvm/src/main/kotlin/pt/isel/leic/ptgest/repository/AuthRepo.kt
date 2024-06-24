package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import java.util.Date
import java.util.UUID

interface AuthRepo {

    fun createUser(name: String, email: String, passwordHash: String, role: Role): UUID

    fun createCompany(id: UUID)

    fun createTrainer(id: UUID, gender: Gender, phoneNumber: String? = null)

    fun createCompanyTrainer(companyId: UUID, trainerId: UUID, capacity: Int)

    fun createTrainee(userId: UUID, birthdate: Date, gender: Gender, phoneNumber: String?)

    fun createPasswordResetToken(tokenHash: String, userId: UUID, expirationDate: Date)

    fun getPasswordResetToken(tokenHash: String): TokenDetails?

    fun removeOldPasswordResetTokens(userId: UUID)

    fun resetPassword(userId: UUID, newPasswordHash: String)

    fun createTokenVersion(userId: UUID): Int

    fun getTokenVersion(userId: UUID): Int?

    fun updateTokenVersion(userId: UUID)
}
