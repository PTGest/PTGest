package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.TokenDetails
import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

interface UserRepo {

    fun createUser(name: String, email: String, passwordHash: String, role: Role): UUID

    fun createCompany(id: UUID)

    fun createIndependentTrainer(id: UUID, gender: Gender, phoneNumber: String? = null)

    fun createPasswordResetToken(userId: UUID, tokenHash: String, expirationDate: Date)

    fun getPasswordResetToken(tokenHash: String): TokenDetails?

    fun resetPassword(userId: UUID, newPasswordHash: String)

    fun createRefreshToken(userId: UUID, tokenHash: String, expirationDate: Date)

    fun getRefreshTokenDetails(tokenHash: String): TokenDetails?

    fun removeRefreshToken(tokenHash: String)

    fun getUserDetails(email: String): UserDetails?

    fun getUserDetails(userId: UUID): UserDetails?
}
