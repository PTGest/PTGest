import CreateCustomWorkoutRequest from "../../../views/user/TrainerViews/models/workouts/CreateCustomWorkoutRequest.ts"
import fetchData from "../../utils/fetchUtils/fetchData.ts"
import router from "../../../plugins/router.ts"
import WorkoutDetails from "../../../views/user/TrainerViews/models/workouts/WorkoutDetails.ts"
import Workouts from "../../../views/user/TrainerViews/models/workouts/Workouts.ts"
import { apiBaseUri } from "../../utils/envUtils.ts"
import handleFilters from "../../utils/fetchUtils/handleFilters.ts"

async function createCustomWorkout(workoutRequest: CreateCustomWorkoutRequest) {
    try {
        const response = await fetchData(`${apiBaseUri}/api/trainer/workout/create`, "POST", workoutRequest)
        router.go(-1)
        return response.resourceId
    } catch (error) {
        console.error("Error creating workout:", error)
        throw error
    }
}

async function getWorkoutDetails(workoutId: string): Promise<WorkoutDetails> {
    const uri = `${apiBaseUri}/api/trainer/workout/` + workoutId

    try {
        const response = await fetchData(uri, "GET", null)
        return new WorkoutDetails(response.details.id, response.details.name, response.details.description, response.details.muscleGroup, response.details.sets)
    } catch (error) {
        console.error("Error fetching workout details:", error)
        throw error
    }
}

async function getWorkouts(filters: Map<string, any> | null): Promise<Workouts> {
    const uri = `${apiBaseUri}/api/trainer/workouts`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Workouts(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching workouts:", error)
        throw error
    }
}

async function likeWorkout(workoutId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/workout/${workoutId}/favorite`

    try {
        await fetchData(uri, "POST", null)
        return
    } catch (error) {
        console.error("Error liking workout:", error)
        throw error
    }
}

async function unlikeWorkout(workoutId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/workout/${workoutId}/unfavorite`

    try {
        await fetchData(uri, "DELETE", null)
        return
    } catch (error) {
        console.error("Error unliking workout:", error)
        throw error
    }
}

export { createCustomWorkout, getWorkoutDetails, getWorkouts, likeWorkout, unlikeWorkout }
