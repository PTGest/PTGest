import Sessions from "../../../views/user/TrainerViews/models/sessions/Sessions.ts"
import handleFilters from "../../utils/handleFilters.ts"
import fetchData from "../../utils/fetchData.ts"

async function getTraineeSessions(traineeId: number, filters: Map<string, any> | null): Promise<Sessions> {
    const uri = `http://localhost:8080/api/trainer/trainee/${traineeId}/sessions`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Sessions(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
export default getTraineeSessions
