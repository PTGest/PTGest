import CreateCustomWorkoutRequest from "../../../views/user/TrainerViews/models/workouts/CreateCustomWorkoutRequest.ts"
import fetchData from "../../utils/fetchData.ts"
import router from "../../../plugins/router.ts"

async function createCustomWorkout(workoutRequest: CreateCustomWorkoutRequest) {
    try {
        const response = await fetchData(`http://localhost:8080/api/trainer/custom-workout`, "POST", workoutRequest)
        router.go(0)
        return response.resourceId
    } catch (error) {
        console.error("Error creating workout:", error)
        throw error
    }
}

export default createCustomWorkout
