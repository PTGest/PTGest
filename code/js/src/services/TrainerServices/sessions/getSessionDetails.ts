import TrainerSessionDetails from "../../../views/user/TrainerViews/models/sessions/TrainerSessionDetails.ts"
import fetchData from "../../utils/fetchData.ts"

async function getSessionDetails(sessionId: number): Promise<TrainerSessionDetails> {
    const uri = `http://localhost:8080/api/trainer/session/${sessionId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export default getSessionDetails
