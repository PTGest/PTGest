import handleFilters from "../../utils/handleFilters.ts"
import fetchData from "../../utils/fetchData.ts"
import TrainerSessions from "../../../views/user/TrainerViews/models/sessions/TrainerSessions.ts"

async function getTrainerSessions(filters: Map<string, any> | null): Promise<TrainerSessions> {
    const uri = "http://localhost:8080/api/trainer/sessions"
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new TrainerSessions(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

export default getTrainerSessions
