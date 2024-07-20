package jobs

import model.TraineeSession
import org.quartz.Job
import org.quartz.JobExecutionContext
import repositories.transaction.TransactionManager
import services.MailService
import java.util.*

class TraineesNotificationJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val mailSender = context?.mergedJobDataMap?.get("mailSender") as? MailService
        val transactionManager = context?.mergedJobDataMap?.get("transactionManager") as? TransactionManager

        if (mailSender == null || transactionManager == null) {
            throw IllegalStateException("Dependencies not found")
        }

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time
        val traineesSessions: HashMap<UUID, List<TraineeSession>> = HashMap()

        transactionManager.run {
            val sessionRepo = it.sessionRepo
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
                                    (session.notes?.let { "Notes: $it\n" } ?: "")
                        }

                mailSender.sendMail(traineeEmail, subject, text)
            }
        }


    }
}