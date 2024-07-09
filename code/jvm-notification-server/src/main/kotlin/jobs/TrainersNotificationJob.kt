package jobs
import model.TrainerSession
import org.quartz.Job
import org.quartz.JobExecutionContext
import repositories.SessionRepo
import services.MailService
import java.util.*
import kotlin.collections.HashMap

class TrainersNotificationJob: Job {
    override fun execute(context: JobExecutionContext) {
        val mailSender = context?.mergedJobDataMap?.get("mailSender") as? MailService
        val sessionRepo = context?.mergedJobDataMap?.get("sessionRepo") as? SessionRepo

        if (mailSender == null || sessionRepo == null) {
            throw IllegalStateException("Dependencies not found")
        }

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.time
        val trainersSessions: HashMap<UUID, List<TrainerSession>> = HashMap()

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