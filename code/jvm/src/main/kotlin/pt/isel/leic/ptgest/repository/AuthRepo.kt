package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Role
import java.util.*

interface AuthRepo {

    fun createUser(name: String, email: String, passwordHash: String, role: Role): UUID

    fun createCompany(id: UUID)

    fun createIndependentTrainer(id: UUID, gender: Gender, phoneNumber: String?)

    fun getUserDetails(email: String): UserDetails?

    fun getUserDetails(userId: UUID): UserDetails?

}