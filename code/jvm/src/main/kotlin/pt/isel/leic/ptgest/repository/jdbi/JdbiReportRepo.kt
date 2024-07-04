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

    override fun getReports(trainerId: UUID, skip: Int, limit: Int?, traineeId: UUID?): List<Report> {
        val traineeCondition = traineeId?.let { "and trainee_id = :traineeId" } ?: ""

        return handle.createQuery(
            """
            select id, date, report, visibility, trainee_id as trainee
            from report_trainer rt join report r on rt.report_id = r.id
            where trainer_id = :trainerId $traineeCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "traineeId" to traineeId
                )
            )
            .mapTo<Report>()
            .list()
    }

    override fun getTotalReports(trainerId: UUID, traineeId: UUID?): Int {
        val traineeCondition = traineeId?.let { "and trainee_id = :traineeId" } ?: ""

        return handle.createQuery(
            """
            select count(*)
            from report_trainer rt join report r on rt.report_id = r.id
            where trainer_id = :trainerId $traineeCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "traineeId" to traineeId
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getReportDetails(trainerId: UUID, reportId: Int): ReportDetails? =
        handle.createQuery(
            """
            select u.name as trainee, r.date, r.report, r.visibility
            from report_trainer rt 
            join report r on rt.report_id = r.id
            join "user" u on r.trainee_id = u.id
            where trainer_id = :trainerId and report_id = :reportId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "reportId" to reportId
                )
            )
            .mapTo<ReportDetails>()
            .firstOrNull()

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
