import fetchData from "../../utils/fetchData.ts";
import CreateReportRequest from "../../../views/user/TrainerViews/models/reports/CreateReportRequest.ts";

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
export default createReport;
