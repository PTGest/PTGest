package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import java.util.*

// todo: check
interface ReportRepo {

    fun createReport(
        traineeId: UUID,
        date: Date,
        report: String,
        visibility: Boolean
    ): Int

    fun getReports(trainerId: UUID, skip: Int, limit: Int?, traineeId: UUID?): List<Report>

    fun getTotalReports(trainerId: UUID, traineeId: UUID?): Int

    fun getReportDetails(trainerId: UUID, reportId: Int): ReportDetails?

    fun editReport(reportId: Int, date: Date, report: String, visibility: Boolean)

    fun deleteReport(reportId: Int)
}
