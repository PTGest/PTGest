import handleFilters from "../../utils/handleFilters.ts";
import Exercises from "../../../views/user/TrainerViews/models/exercises/Exercises.ts";
import fetchData from "../../utils/fetchData.ts";
export default async function getExercises(filters: Map<string,any> | null): Promise<Exercises> {
    const uri = "http://localhost:8080/api/trainer/exercises";
    let postFiltersUri = uri;
    console.log("filters", filters)
    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters);
    }
    console.log("postFiltersUri", postFiltersUri);
    try {
        const response = await fetchData( postFiltersUri, "GET", null);
        console.log("response", response);
        return new Exercises(response.details.items, response.details.total);
    } catch (error) {
        console.error("Error fetching exercises:", error);
        throw error;
    }
}

