package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

interface UserRepo {

    fun createUser(name: String, email: String, passwordHash: String, role: Role): UUID

    fun createCompany(id: UUID)

    fun createTrainer(id: UUID, gender: Gender, phoneNumber: String? = null)

    fun createCompanyTrainer(companyId: UUID, trainerId: UUID)

    fun createTrainee(userId: UUID, birthdate: Date, gender: Gender, phoneNumber: String?)

    fun associateTraineeToTrainer(traineeId: UUID, trainerId: UUID)

    fun resetPassword(userId: UUID, newPasswordHash: String)

    fun createToken(tokenHash: String, userId: UUID, expirationDate: Date)

    fun removeToken(tokenHash: String)

    fun createRefreshToken(tokenHash: String)

    fun getRefreshTokenDetails(tokenHash: String): TokenDetails?

    fun createPasswordResetToken(tokenHash: String)

    fun getPasswordResetToken(tokenHash: String): TokenDetails?

    fun getUserDetails(email: String): UserDetails?

    fun getUserDetails(userId: UUID): UserDetails?
}
