
import handleFilters from "../../utils/handleFilters.ts";
import fetchData from "../../utils/fetchData.ts";
import Reports from "../../../views/user/TrainerViews/models/reports/Reports.ts";


export default async function getReports(filters: Map<string, any> | null): Promise<Reports> {
    const uri = "http://localhost:8080/api/trainer/reports"
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }

    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Reports(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
