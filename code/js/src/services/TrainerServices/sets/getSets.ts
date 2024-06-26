import handleFilters from "../../utils/handleFilters.ts"
import fetchData from "../../utils/fetchData.ts"
import Sets from "../../../views/user/TrainerViews/models/sets/Sets.ts"

export default async function getSets(filters: Map<string, any> | null): Promise<Sets> {
    const uri = "http://localhost:8080/api/trainer/sets"
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }

    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Sets(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
