import CreateCustomExerciseRequest from "../../../views/user/TrainerViews/models/exercises/CreateCustomExerciseRequest.ts";
import fetchData from "../../utils/fetchData.ts";
import router from "../../../plugins/router.ts";
import Error from "../../../views/Error.vue";


async function createExercise(exercise: CreateCustomExerciseRequest): Promise<void> {
    try {
        const response = await fetchData(`http://localhost:8080/api/trainer/custom-exercise`,'POST', exercise);
        router.go(0);
        return response.resourceId;

    } catch (error : any) {
        console.log(error);
        
        throw error;
    }
}

export default createExercise;
