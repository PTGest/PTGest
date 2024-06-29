import CreateSessionRequest from "../../../views/user/TrainerViews/models/sessions/CreateSessionRequest.ts"
import fetchData from "../../utils/fetchData.ts"

async function editSession(sessionId: number, sessionData: CreateSessionRequest): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/session/${sessionId}/edit`
    try {
        const response = await fetchData(uri, "PUT", sessionData)
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export default editSession
