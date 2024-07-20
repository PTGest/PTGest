import jobs.TraineesNotificationJob
import jobs.TrainersNotificationJob
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.quartz.DateBuilder
import org.quartz.JobBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory
import pt.isel.leic.ptgest.repository.jdbi.transaction.JdbiTransactionManager
import services.MailService

fun main() {
    val logger = LoggerFactory.getLogger("Main")

    try {
        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(ServerConfig.dbUrl)
                currentSchema = "prod"
            }
        ).apply {
            installPlugin(KotlinPlugin())
            installPlugin(PostgresPlugin())
        }

        val mailService = MailService(ServerConfig.mailUsername, ServerConfig.mailPassword)
        val transactionManager = JdbiTransactionManager(jdbi)
        val schedulerFactory = StdSchedulerFactory()
        val scheduler = schedulerFactory.scheduler

        scheduler.start()

        val traineesNotificationJob = JobBuilder.newJob(TraineesNotificationJob::class.java)
            .withIdentity("traineesNotificationJob")
            .build()
            .apply {
                jobDataMap["mailSender"] = mailService
                jobDataMap["transactionManager"] = transactionManager
            }

        val trainersNotificationJob = JobBuilder.newJob(TrainersNotificationJob::class.java)
            .withIdentity("trainersNotificationJob")
            .build()
            .apply {
                jobDataMap["mailSender"] = mailService
                jobDataMap["transactionManager"] = transactionManager
            }

        val traineesNotificationTrigger = TriggerBuilder.newTrigger()
            .withIdentity("traineesNotificationTrigger")
            .startAt(DateBuilder.todayAt(18, 0, 0))
            .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(60 * 60 * 24))
            .forJob(traineesNotificationJob)
            .build()

        val trainersNotificationTrigger = TriggerBuilder.newTrigger()
            .withIdentity("trainersNotificationTrigger")
            .startAt(DateBuilder.todayAt(18, 0, 0))
            .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(60 * 60 * 24))
            .forJob(trainersNotificationJob)
            .build()

        scheduler.scheduleJob(traineesNotificationJob, traineesNotificationTrigger)
        scheduler.scheduleJob(trainersNotificationJob, trainersNotificationTrigger)
    } catch (e: Exception) {
        logger.error("An error occurred while starting the application", e)
    }
}