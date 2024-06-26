
import fetchData from "../../utils/fetchData.ts";
import ReportDetails from "../../../views/user/TrainerViews/models/reports/ReportDetails.ts";

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
export default getReportDetails;
