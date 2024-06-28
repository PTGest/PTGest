import fetchData from "../../utils/fetchData.ts";
import CancelSessionRequest from "../../../views/user/TrainerViews/models/sessions/CancelSessionRequest.ts";
import router from "../../../plugins/router.ts";

async function cancelSession(sessionId: number, reason: CancelSessionRequest): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/session/${sessionId}/cancel`
    try {
        const response = await fetchData(uri, "POST", reason)
        router.back()
        return response;

    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export default cancelSession
