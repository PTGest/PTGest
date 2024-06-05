import fetchData from "../../utils/fetchData.ts";
import SetDetails from "../../../views/user/TrainerViews/models/sets/SetDetails.ts";

export default async function getSetDetails(setId: number): Promise<SetDetails> {
    const uri = "http://localhost:8080/api/trainer/set/" + setId;

    try {
        const response = await fetchData( uri, "GET", null );
        return new SetDetails(
            response.details.name,
            response.details.notes,
            response.details.type,
            response.details.setExerciseDetails);
    } catch (error) {
        console.error("Error fetching exercises:", error);
        throw error;
    }
}
