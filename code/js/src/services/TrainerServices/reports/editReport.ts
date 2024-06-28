import EditReportRequest from "../../../views/user/TrainerViews/models/reports/CreateReportRequest.ts";
import fetchData from "../../utils/fetchData.ts";
import router from "../../../plugins/router.ts";

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
export default editReport;
