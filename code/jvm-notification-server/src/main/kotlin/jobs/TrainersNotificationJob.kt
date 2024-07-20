package jobs

import model.TrainerSession
import org.quartz.Job
import org.quartz.JobExecutionContext
import repositories.transaction.TransactionManager
import services.MailService
import java.util.*

class TrainersNotificationJob : Job {
    override fun execute(context: JobExecutionContext) {
        val mailSender = context.mergedJobDataMap?.get("mailSender") as? MailService
        val transactionManager = context.mergedJobDataMap?.get("transactionManager") as? TransactionManager

        if (mailSender == null || transactionManager == null) {
            throw IllegalStateException("Dependencies not found")
        }

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time
        val trainersSessions: HashMap<UUID, List<TrainerSession>> = HashMap()

        transactionManager.run {
            val sessionRepo = it.sessionRepo

            sessionRepo.getTrainers(tomorrow).forEach { trainerId ->
                trainersSessions[trainerId] = sessionRepo.getTrainerSessions(trainerId, tomorrow)
            }

            trainersSessions.forEach { (trainerId, sessions) ->
                val trainerEmail = sessionRepo.getUserEmail(trainerId)
                val subject = "Scheduled sessions for tomorrow"
                val text = "Hello trainer,\n\n" +
                        "You have the following sessions scheduled for tomorrow:\n\n" +
                        sessions.joinToString("\n") { session ->
                            "Trainee: ${session.traineeName}\n" +
                                    "Begin date: ${session.beginDate}\n" +
                                    "End date: ${session.endDate}\n" +
                                    "Location: ${session.location}\n"
                        }

                mailSender.sendMail(trainerEmail, subject, text)
            }
        }
    }
}