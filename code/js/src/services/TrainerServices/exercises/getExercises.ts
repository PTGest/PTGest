import handleFilters from "../../utils/handleFilters.ts";
import Exercises from "../../../views/user/TrainerViews/models/exercises/Exercises.ts";
import fetchData from "../../utils/fetchData.ts";
export default async function getExercises(filters: string[]): Promise<Exercises> {
    const uri = "http://localhost:8080/api/trainer/exercises";
    let postFiltersUri = uri;

    if (filters.length !== 0) {
        postFiltersUri = handleFilters(uri, filters);
    }

    try {
        const response = await fetchData( postFiltersUri, "GET", null);
        return new Exercises(response.details.exercises, response.details.total);
    } catch (error) {
        console.error("Error fetching exercises:", error);
        throw error;
    }
}

