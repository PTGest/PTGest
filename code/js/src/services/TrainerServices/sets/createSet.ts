import fetchData from "../../utils/fetchData.ts";
import CreateCustomSetRequest from "../../../views/user/TrainerViews/models/sets/CreateCustomSetRequest.ts";
import mapToObject from "../../utils/mapToObject.ts";
import router from "../../../plugins/router.ts";

// Function to convert Map to Object

async function createSet(setData: CreateCustomSetRequest): Promise<number> {
    try {
        // Convert setData to Object
        setData.setExercises.map((setExercise) => { setExercise.details = mapToObject(setExercise.details) });
        const response = await fetchData(`http://localhost:8080/api/trainer/custom-set`,'POST', setData);
        router.go(0);
        return response.resourceId;
    } catch (error) {
        console.error("Error fetching exercises:", error);
        throw error;
    }
}

export default createSet;
