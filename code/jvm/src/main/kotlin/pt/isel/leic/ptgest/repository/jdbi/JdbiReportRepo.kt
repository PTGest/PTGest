package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.repository.ReportRepo
import java.util.*

class JdbiReportRepo(private val handle: Handle) : ReportRepo {

    override fun createReport(traineeId: UUID, date: Date, report: String, visibility: Boolean): Int =
        handle.createUpdate(
            """
            insert into report (trainee_id, date, report, visibility)
            values (:traineeId, :date, :report, :visibility)
            """
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date,
                    "report" to report,
                    "visibility" to visibility
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getReports(traineeId: UUID, skip: Int, limit: Int?): List<Report> =
        handle.createQuery(
            """
            select id, date, visibility
            from report
            where trainee_id = :traineeId
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "skip" to skip,
                    "limit" to limit
                )
            )
            .mapTo<Report>()
            .list()

    override fun getTotalReports(traineeId: UUID): Int =
        handle.createQuery(
            """
            select count(*)
            from report
            where trainee_id = :traineeId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId
                )
            )
            .mapTo<Int>()
            .one()

    override fun getReportDetails(traineeId: UUID, reportId: Int): ReportDetails? =
        handle.createQuery(
            """
            select u.name as trainee, r.date, r.report, r.visibility
            from report r
            join "user" u on r.trainee_id = u.id
            where trainee_id = :traineeId and report_id = :reportId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "reportId" to reportId
                )
            )
            .mapTo<ReportDetails>()
            .firstOrNull()

    override fun reportBelongsToTrainer(trainerId: UUID, reportId: Int): Boolean =
        handle.createQuery(
            """
            select exists(
                select 1
                from report_trainer
                where report_id = :reportId and trainer_id = :trainerId
            )
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "reportId" to reportId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Boolean>()
            .one()

    override fun editReport(reportId: Int, date: Date, report: String, visibility: Boolean) {
        handle.createUpdate(
            """
            update report
            set date = :date, report = :report, visibility = :visibility
            where id = :reportId
            """
        )
            .bindMap(
                mapOf(
                    "date" to date,
                    "reportId" to reportId,
                    "report" to report,
                    "visibility" to visibility
                )
            )
            .execute()
    }

    override fun deleteReport(reportId: Int) {
        handle.createUpdate(
            """
            delete from report
            where id = :reportId
            """
        )
            .bind("reportId", reportId)
            .execute()
    }
}
