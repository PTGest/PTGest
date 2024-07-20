package repositories

import model.TraineeSession
import model.TrainerSession
import java.util.*

interface SessionRepo {

    fun getUserEmail(userId: UUID): String

    fun getTrainers(date: Date): List<UUID>

    fun getTrainees(date: Date): List<UUID>

    fun getTrainerSessions(trainerId: UUID, date: Date): List<TrainerSession>

    fun getTraineeSessions(traineeId: UUID, date: Date): List<TraineeSession>

}