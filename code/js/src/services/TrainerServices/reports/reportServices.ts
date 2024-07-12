import fetchData from "../../utils/fetchUtils/fetchData.ts"
import CreateReportRequest from "../../../views/user/TrainerViews/models/reports/CreateReportRequest.ts"
import router from "../../../plugins/router.ts"
import ReportDetails from "../../../views/user/TrainerViews/models/reports/ReportDetails.ts"
import Reports from "../../../views/user/TrainerViews/models/reports/Reports.ts"
import handleFilters from "../../utils/fetchUtils/handleFilters.ts"
import { apiBaseUri } from "../../utils/envUtils.ts"
import EditReportRequest from "../../../views/user/TrainerViews/models/reports/EditReportRequest.ts"

async function createReport(reportData: CreateReportRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/trainee/${reportData.traineeId}/report`
    try {
        await fetchData(uri, "POST", reportData)
        return
    } catch (error) {
        console.error("Error creating report:", error)
        throw error
    }
}
async function editReport(reportId: number, reportData: EditReportRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/trainee/${reportData.traineeId}/report/${reportId}/edit`
    try {
        await fetchData(uri, "PUT", reportData)
        router.go(-1)
        return
    } catch (error) {
        console.error("Error editing report:", error)
        throw error
    }
}
async function getReportDetails(traineeId: string, reportId: number): Promise<ReportDetails> {
    const uri = `${apiBaseUri}/api/trainer/trainee/${traineeId}/report/${reportId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching report details:", error)
        throw error
    }
}
async function getReports(traineeId: string, filters: Map<string, any> | null): Promise<Reports> {
    const uri = `${apiBaseUri}/api/trainer/trainee/${traineeId}/reports`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }

    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Reports(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching reports:", error)
        throw error
    }
}

export { createReport, editReport, getReportDetails, getReports }
