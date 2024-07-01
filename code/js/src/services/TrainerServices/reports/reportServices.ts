import fetchData from "../../utils/fetchData.ts";
import CreateReportRequest from "../../../views/user/TrainerViews/models/reports/CreateReportRequest.ts";
import EditReportRequest from "../../../views/user/TrainerViews/models/reports/CreateReportRequest.ts";
import router from "../../../plugins/router.ts";
import ReportDetails from "../../../views/user/TrainerViews/models/reports/ReportDetails.ts";
import Reports from "../../../views/user/TrainerViews/models/reports/Reports.ts";
import handleFilters from "../../utils/handleFilters.ts";

async function createReport(reportData: CreateReportRequest): Promise<void> {
    const uri = "http://localhost:8080/api/trainer/report"
    try {
        await fetchData(uri, "POST", reportData);
        return
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function editReport(reportId: number,reportData: EditReportRequest): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/report/${reportId}/edit`
    try {
        await fetchData(uri, "PUT", reportData);
        router.go(-1)
        return
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function getReportDetails(reportId: number): Promise<ReportDetails> {
    const uri = "http://localhost:8080/api/trainer/report/" + reportId
    try {
        const response = await fetchData(uri, "GET", null);
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function getReports(filters: Map<string, any> | null): Promise<Reports> {
    const uri = "http://localhost:8080/api/trainer/reports"
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }

    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Reports(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export {
    createReport,
    editReport,
    getReportDetails,
    getReports
};
