import fetchData from "../../utils/fetchData.ts"
import WorkoutDetails from "../../../views/user/TrainerViews/models/workouts/WorkoutDetails.ts"

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
export default getWorkoutDetails
