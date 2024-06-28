import CreateSessionRequest from "../../../views/user/TrainerViews/models/sessions/CreateSessionRequest.ts"
import fetchData from "../../utils/fetchData.ts"

async function createSession(sessionData: CreateSessionRequest): Promise<void> {
    const uri = "http://localhost:8080/api/trainer/session"
    try {
        return await fetchData(uri, "POST", sessionData)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export default createSession
