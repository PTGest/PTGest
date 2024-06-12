import fetchData from "../../utils/fetchData.ts";
import TrainerTrainees from "../../../views/user/TrainerViews/models/trainees/TrainerTrainees.ts";

async function getTrainerTrainees(
    skip: number | null,
    limit: number | null,
    availability: string | null,
    gender: string | null,
): Promise<TrainerTrainees> {

    let url = `http://localhost:8080/api/trainer/trainees`;
    // Construct URL with skip and limit parameters
    if (skip != null) {
        url += `?skip=${skip}`;
    }
    if (limit != null) {
        url += `&limit=${limit}`;
    }
    if (availability != null) {
        url += `&availability=${availability}`;
    }
    if (gender != null) {
        url += `&gender=${gender}`;
    }

    try {
        const response = await fetchData(url,'GET', null);
        return new TrainerTrainees(response.details.items, response.details.total);
    } catch (error) {
        console.error("Error creating set:", error);
        throw error;
    }
}

export default getTrainerTrainees;
