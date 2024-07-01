import CreateCustomWorkoutRequest from "../../../views/user/TrainerViews/models/workouts/CreateCustomWorkoutRequest.ts"
import fetchData from "../../utils/fetchData.ts"
import router from "../../../plugins/router.ts"
import WorkoutDetails from "../../../views/user/TrainerViews/models/workouts/WorkoutDetails.ts";
import Workouts from "../../../views/user/TrainerViews/models/workouts/Workouts.ts";

async function createCustomWorkout(workoutRequest: CreateCustomWorkoutRequest) {
    try {
        const response = await fetchData(`http://localhost:8080/api/trainer/custom-workout`, "POST", workoutRequest)
        router.go(-1)
        return response.resourceId
    } catch (error) {
        console.error("Error creating workout:", error)
        throw error
    }
}

async function getWorkoutDetails(workoutId: string): Promise<WorkoutDetails> {
    const uri = "http://localhost:8080/api/trainer/workout/" + workoutId

    try {
        const response = await fetchData(uri, "GET", null)
        return new WorkoutDetails(response.details.id, response.details.name, response.details.description, response.details.muscleGroup, response.details.sets)
    } catch (error) {
        console.error("Error fetching workout details:", error)
        throw error
    }
}


async function getWorkouts(): Promise<Workouts> {
    try {
        const response = await fetchData(`http://localhost:8080/api/trainer/workouts`, "GET", null)
        return new Workouts(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching workouts:", error)
        throw error
    }
}

export {
    createCustomWorkout,
    getWorkoutDetails,
    getWorkouts
}
