import handleFilters from "../utils/handleFilters.ts";
import fetchData from "../utils/fetchData.ts";
import Sets from "../../views/user/TrainerViews/models/Sets.ts";

export default async function getSets(filters: string[]): Promise<Sets> {
    const uri = "http://localhost:8080/api/trainer/sets";
    let postFiltersUri = uri;

    if (filters.length !== 0) {
        postFiltersUri = handleFilters(uri, filters);
    }

    try {
        const response = await fetchData( postFiltersUri, "GET", null );
        return new Sets(response.details.sets, response.details.total);
    } catch (error) {
        console.error("Error fetching exercises:", error);
        throw error;
    }
}
