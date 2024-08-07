package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import java.util.*

interface ReportRepo {

    fun createReport(
        traineeId: UUID,
        date: Date,
        report: String,
        visibility: Boolean
    ): Int

    fun getTraineeReports(traineeId: UUID, skip: Int, limit: Int?): List<Report>

    fun getTotalTraineeReports(traineeId: UUID): Int

    fun getTrainerReports(traineeId: UUID, skip: Int, limit: Int?): List<Report>

    fun getTotalTrainerReports(traineeId: UUID): Int

    fun getReportDetails(traineeId: UUID, reportId: Int): ReportDetails?

    fun reportBelongsToTrainer(trainerId: UUID, reportId: Int): Boolean

    fun editReport(reportId: Int, date: Date, report: String, visibility: Boolean)

    fun deleteReport(reportId: Int)
}
