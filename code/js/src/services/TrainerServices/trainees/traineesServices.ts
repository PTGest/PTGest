import fetchData from "../../utils/fetchData.ts"
import TrainerTrainees from "../../../views/user/TrainerViews/models/trainees/TrainerTrainees.ts"

async function getTrainerTrainees(skip: number | null, limit: number | null, name: string | null, gender: string | null): Promise<TrainerTrainees> {
    let url = `http://localhost:8080/api/trainer/trainees`
    // Construct URL with skip and limit parameters
    const queryParams: string[] = [];

// Add each parameter to the array if it exists
    if (skip != null) {
        queryParams.push(`skip=${skip}`);
    }
    if (limit != null) {
        queryParams.push(`limit=${limit}`);
    }
    if (name != null) {
        queryParams.push(`name=${name}`);
    }
    if (gender != null) {
        queryParams.push(`gender=${gender}`);
    }

// Construct the final URL with query parameters if there are any
    if (queryParams.length > 0) {
        url += `?${queryParams.join('&')}`;
    }

    try {
        const response = await fetchData(url, "GET", null)
        return new TrainerTrainees(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error creating set:", error)
        throw error
    }
}

export default getTrainerTrainees
