package jobs

import model.TraineeSession
import org.quartz.Job
import org.quartz.JobExecutionContext
import repositories.SessionRepo
import services.MailService
import java.util.*
import kotlin.collections.HashMap

class TraineesNotificationJob: Job {
    override fun execute(context: JobExecutionContext?) {
        val mailSender = context?.mergedJobDataMap?.get("mailSender") as? MailService
        val sessionRepo = context?.mergedJobDataMap?.get("sessionRepo") as? SessionRepo

        if (mailSender == null || sessionRepo == null) {
            throw IllegalStateException("Dependencies not found")
        }

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time
        val traineesSessions: HashMap<UUID, List<TraineeSession>> = HashMap()

        sessionRepo.getTrainees(tomorrow).forEach { trainerId ->
            traineesSessions[trainerId] = sessionRepo.getTraineeSessions(trainerId, tomorrow)
        }

        traineesSessions.forEach { (traineeId, sessions) ->
            val traineeEmail = sessionRepo.getUserEmail(traineeId)
            val subject = "Scheduled sessions for tomorrow"
            val text = "Hello trainee,\n\n" +
                    "You have the following sessions scheduled for tomorrow:\n\n" +
                    sessions.joinToString("\n") { session ->
                        "Trainer: ${session.trainerName}\n" +
                        "Begin date: ${session.beginDate}\n" +
                        "End date: ${session.endDate}\n" +
                        "Location: ${session.location}\n" +
                        session.notes?.let { "Notes: $it\n" }
                    }

            mailSender.sendMail(traineeEmail, subject, text)
        }
    }
}